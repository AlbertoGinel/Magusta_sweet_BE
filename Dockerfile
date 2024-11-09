# Stage 1: Build the application using Maven and OpenJDK 17
FROM maven:3.8.4-openjdk-17 AS builder

WORKDIR /usr/src/app

# Copy pom.xml and download dependencies first (to cache dependencies)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the application code
COPY . .

# Ensure the mvnw script has execute permissions
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Run Maven to build the JAR file, skipping tests for faster builds
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application with Java 17
FROM openjdk:17-jdk

WORKDIR /usr/local/app

# Copy the built JAR file from the builder stage
COPY --from=builder /usr/src/app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Set the environment variable for the OpenAI API key
ENV OPENAI_API_KEY=${OPENAI_API_KEY}

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
