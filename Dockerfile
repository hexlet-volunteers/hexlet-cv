# ---- Stage 1: сборка фронтенда ----
FROM node:22-bookworm-slim AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
# --ignore-scripts: prepare ставит husky-хуки и без .git падает
RUN npm ci --ignore-scripts
COPY frontend/ ./
RUN npm run build

# ---- Stage 2: сборка бэкенда ----
# Node нужен и здесь: compileJava генерирует код из TypeSpec (api/) через npm.
FROM node:22-bookworm-slim AS build
COPY --from=eclipse-temurin:24-jdk /opt/java/openjdk /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
WORKDIR /app

COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties versions.properties ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon --version

COPY api ./api
COPY config ./config
COPY src ./src

# Собранный фронт: статика отдаётся Spring из classpath:static, а index.html служит
# оболочкой для SEO-страниц, маршрутов SPA и Inertia-шаблона (до его удаления в #1111).
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static/
RUN cp src/main/resources/static/index.html src/main/resources/templates/app.html

RUN ./gradlew --no-daemon bootJar -x test

# ---- Stage 3: запуск ----
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app
RUN adduser -D -H -h /app appuser
COPY --from=build /app/build/libs/*.jar app.jar
RUN chown -R appuser:appuser /app
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
