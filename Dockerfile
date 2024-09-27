# Start with an OpenJDK base image
FROM openjdk:17-jdk-alpine

# Set an environment variable for the target JAR file
ARG JAR_FILE=target/demo-webapp-0.0.1-SNAPSHOT.jar

# Copy the Spring Boot JAR file to the container
COPY ${JAR_FILE} app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app.jar"]
