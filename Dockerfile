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

