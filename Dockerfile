FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the maven wrapper and pom file first to leverage Docker cache
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable 
RUN chmod +x ./mvnw

# Download dependencies first (this layer will be cached)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Use the jar from the build
ENTRYPOINT ["java", "-jar", "/app/target/user-auth-service-0.0.1-SNAPSHOT.jar"]