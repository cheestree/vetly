# Stage 1: Build the app
FROM gradle:8.14.0-jdk24-alpine AS build
WORKDIR /app

# Copy Gradle wrapper and config files
COPY build.gradle* settings.gradle* gradlew ./
COPY gradle ./gradle

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached unless build.gradle changes)
RUN ./gradlew --no-daemon dependencies

# Copy source code
COPY . .

# Build the JAR
RUN ./gradlew bootJar --no-daemon

# Stage 2: Run the app with a minimal image
FROM eclipse-temurin:24-jre-alpine

# Set working directory
WORKDIR /app

# Copy the jar from the builder stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]