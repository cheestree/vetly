# Compile code, resolve dependencies, run Gradle, build Jar.
FROM gradle:8.5-jdk21-alpine AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradle.properties gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon --stacktrace

COPY . .

RUN ./gradlew bootJar --no-daemon --stacktrace -x test

# Production image.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/build/libs/*.jar app.jar

USER spring:spring

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:-} -Dserver.port=${PORT:-8080} -jar app.jar"]
