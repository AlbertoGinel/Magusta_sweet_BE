# Stage 1: Build the JAR file with Java 17
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /usr/src/app
COPY . .

# Ensure the mvnw script has execute permissions
# RUN chmod +x mvnw

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Run Maven to build the JAR file, skipping tests for faster builds
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application with Java 17
FROM openjdk:17-jdk
WORKDIR /usr/local/app
COPY --from=builder /usr/src/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

ENV OPENAI_API_KEY=${OPENAI_API_KEY}


2. Port Mismatch

    Problem: Render may expect your service to run on a specific port (often 10000 for HTTP by default), but if your Tomcat server is configured for another port, Render might not connect properly, leading to repeated connection attempts and EOFException.
    Solution: Confirm that Tomcat is set to listen on the correct port. In your Dockerfile or docker-compose.yml, make sure the correct port (e.g., 8080 if you specified it) is both exposed and mapped to Render’s expected port.

