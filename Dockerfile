# ---- Stage 1: Frontend build ----
FROM node:20 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
# Backend отправляет Home/Index и только pageSections; Home.tsx ждёт articles, trainingPrograms, performanceReview — обёртка с дефолтами
RUN <<'SCRIPT'
mkdir -p src/pages/Home
cat > src/pages/Home/Index.tsx << 'EOF'
import Index from '../Home'

export default function HomeIndex(props: Record<string, unknown>) {
  const p = props ?? {}
  return <Index
    articles={(p.articles as unknown[]) ?? []}
    trainingPrograms={(p.trainingPrograms as unknown[]) ?? []}
    performanceReview={(p.performanceReview as unknown[]) ?? []}
    pageSections={(p.pageSections as unknown[]) ?? []}
  />
}
EOF
SCRIPT
RUN npx vite build

# ---- Stage 2: Backend build ----
FROM gradle:8.14.1-jdk17 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Скачиваем зависимости
RUN gradle dependencies --no-daemon || true

# Копируем конфиг checkstyle и исходники
COPY config ./config
COPY src ./src

# Копируем фронтенд: assets в static и META-INF, app.html — напрямую из dist (обходим repo app.html без скриптов)
COPY --from=frontend-build /app/frontend/dist /app/src/main/resources/static/
RUN mkdir -p /app/src/main/resources/META-INF/resources && \
    cp -r /app/src/main/resources/static/assets /app/src/main/resources/META-INF/resources/ && \
    cp /app/src/main/resources/static/vite.svg /app/src/main/resources/META-INF/resources/ 2>/dev/null || true

# inertia4j использует templates/app.html — подменяем repo-версию на собранный index.html со скриптами
COPY --from=frontend-build /app/frontend/dist/index.html /app/src/main/resources/templates/app.html
RUN sed -i 's|<div id="app">[^<]*</div>|<div id="app" data-page='"'"'@PageObject@'"'"'></div>|' \
    /app/src/main/resources/templates/app.html && \
    grep -qE 'script[^>]*type=.*module' /app/src/main/resources/templates/app.html || (echo "ERROR: Script tags missing in app.html - frontend will not load" && exit 1)

RUN gradle build --no-daemon -x test

# ---- Stage 3: Runtime ----
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app

# Создаем пользователя (безопасность)
RUN adduser -D -H -h /app appuser

# Копируем jar из этапа сборки
COPY --from=build /app/build/libs/*.jar app.jar

# Права доступа
RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]