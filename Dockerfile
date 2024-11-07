# Stage 1: Build the JAR file
FROM maven:3.8.1-openjdk-11 AS builder
WORKDIR /usr/src/app
COPY . .

# Ensure the mvnw script has execute permissions
RUN chmod +x mvnw

# Run Maven to build the JAR file, skipping tests for faster builds
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:11-jdk
WORKDIR /usr/local/app
COPY --from=builder /usr/src/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
