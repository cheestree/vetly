FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app

COPY build.gradle* settings.gradle* gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY . .
RUN ./gradlew bootJar --no-daemon --stacktrace

FROM eclipse-temurin:24-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar
COPY src/main/resources/firebase-service-account.json ./

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]