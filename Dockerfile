# Stage 1: Build the JAR file
FROM docker.io/library/maven:3.8.1-openjdk-11 AS builder
WORKDIR /usr/src/app
COPY . .
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM docker.io/library/openjdk:11-jdk
WORKDIR /usr/local/app
COPY --from=builder /usr/src/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]