#   Compile code, resolve dependencies, run Gradle, build Jar
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app
COPY build.gradle* settings.gradle* gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY . .

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon --stacktrace

#   Production image
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE $PORT
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]