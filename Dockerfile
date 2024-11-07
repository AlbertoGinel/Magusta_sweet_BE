# Stage 1: Build the JAR file
FROM maven:3.8.4-openjdk-21 AS builder
WORKDIR /usr/src/app
COPY . .
# Run Maven to build the JAR file, skipping tests for faster builds
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:21-jdk
WORKDIR /usr/local/app
# Copy the JAR file from the builder stage
COPY --from=builder /usr/src/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]