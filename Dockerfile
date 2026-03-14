# Сборка
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Кэш зависимостей Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .
COPY gradle/libs.versions.toml gradle/

# Скачивание зависимостей (без сборки приложения)
RUN ./gradlew dependencies --no-daemon || true

# Исходники и сборка
COPY src src
COPY frontend frontend

# Сборка frontend (если нужна интеграция в бэкенд)
RUN cd frontend && npm ci && npm run build

# Копирование собранного frontend в static (если используется)
# RUN cp -r frontend/dist/* src/main/resources/static/  # раскомментировать при необходимости

RUN ./gradlew bootJar --no-daemon -x test

# Рантайм
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN adduser -D appuser
USER appuser

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]