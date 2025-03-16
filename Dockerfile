FROM eclipse-temurin:17-jdk

WORKDIR /app

# Install wait-for-it script for service health checking
RUN apt-get update && apt-get install -y wget && \
    wget -q -O /usr/local/bin/wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && \
    chmod +x /usr/local/bin/wait-for-it.sh

# Copy the Maven wrapper files
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Fix file permissions for mvnw (common issue on Windows)
RUN chmod +x ./mvnw

# Build the application
RUN ./mvnw package -DskipTests

# Create a startup script
RUN echo '#!/bin/bash\n\
echo "Waiting for MongoDB to be ready..."\n\
wait-for-it.sh mongodb:27017 -t 60\n\
if [ $? -ne 0 ]; then\n\
  echo "MongoDB connection failed!"\n\
  exit 1\n\
fi\n\
echo "MongoDB is up - starting application"\n\
java -jar target/user-auth-service-0.0.1-SNAPSHOT.jar\n\
' > /app/start.sh && chmod +x /app/start.sh

# Run the application with the startup script
CMD ["/app/start.sh"]