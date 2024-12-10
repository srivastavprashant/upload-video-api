# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Maven/Gradle files
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copy the project source
COPY src src

# Package the application
RUN ./mvnw package -DskipTests

# Make port 8080 available outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app/target/upload-service.jar"]