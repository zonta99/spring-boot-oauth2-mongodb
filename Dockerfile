FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

# Copy maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY .gitattributes .
# Make the mvnw file executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Create application.yml with MongoDB configuration directly in the container
RUN mkdir -p src/main/resources
RUN echo "spring:" > src/main/resources/application.yml
RUN echo "  data:" >> src/main/resources/application.yml
RUN echo "    mongodb:" >> src/main/resources/application.yml
RUN echo "      uri: \${SPRING_DATA_MONGODB_URI:mongodb://mongoadmin:secret@mongodb:27017/userdb?authSource=admin}" >> src/main/resources/application.yml
# FIXED: Removed self-references by using direct values instead of referencing the same properties
RUN echo "      host: mongodb" >> src/main/resources/application.yml
RUN echo "      port: 27017" >> src/main/resources/application.yml
RUN echo "      database: userdb" >> src/main/resources/application.yml
RUN echo "      username: mongoadmin" >> src/main/resources/application.yml
RUN echo "      password: secret" >> src/main/resources/application.yml
RUN echo "      authentication-database: admin" >> src/main/resources/application.yml
RUN echo "      auto-index-creation: true" >> src/main/resources/application.yml

# Add app auth properties for JWT 
RUN echo "app:" >> src/main/resources/application.yml
RUN echo "  auth:" >> src/main/resources/application.yml
RUN echo "    tokenSecret: \${TOKEN_SECRET:cff8c43509cf688e99090dbebd3aae0ed06a6baf65fa1438e3fbb3326dc59e9d}" >> src/main/resources/application.yml
RUN echo "    tokenExpirationMsec: 864000000" >> src/main/resources/application.yml

# Build the application
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency

# Copy the dependency application layer
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Set the entry point
ENTRYPOINT ["java","-cp","app:app/lib/*","com.example.userauthservice.UserAuthServiceApplication"]