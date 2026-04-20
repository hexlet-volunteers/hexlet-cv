# Stage 1 : build
FROM eclipse-temurin:24-jdk AS builder
WORKDIR /app

COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY build.gradle.kts settings.gradle.kts gradle.properties versions.properties ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon 2>/dev/null; true

COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2 : runtime
FROM eclipse-temurin:24-jre AS runtime
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/hexlet-cv-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]