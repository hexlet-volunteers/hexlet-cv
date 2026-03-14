# Build stage (Java 24 to match build.gradle.kts toolchain)
FROM eclipse-temurin:24-jdk-alpine AS build
WORKDIR /app

# Install Node.js for frontend build (Alpine)
RUN apk add --no-cache nodejs npm

# Gradle wrapper and config
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties .

# Download dependencies (cache layer)
RUN ./gradlew dependencies --no-daemon || true

# Source and frontend
COPY src src
COPY frontend frontend

# Build frontend
RUN cd frontend && npm ci && npm run build

# Build Spring Boot JAR
RUN ./gradlew bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app

RUN adduser -D appuser
USER appuser

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
