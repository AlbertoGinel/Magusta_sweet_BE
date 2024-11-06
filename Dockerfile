# Use an official Java runtime as a parent image
FROM openjdk:21-jdk
# Set the working directory in the container, Docker creates
WORKDIR /usr/local/app
# Copy the Spring Boot application JAR file into the container
COPY target/*.jar app.jar
# Expose the port that the application will run on
EXPOSE 8080
# Run the command to start the Spring Boot application when the container launches
ENTRYPOINT ["java","-jar","app.jar"]