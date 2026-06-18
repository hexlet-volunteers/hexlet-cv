# Деплой hexlet-cv на Render.com

Пошаговая инструкция по развёртыванию приложения на [Render.com](https://render.com) с использованием Docker и PostgreSQL.

---

## Предварительные требования

- Аккаунт на [render.com](https://render.com) — при его отсутствии [зарегистрируйтесь](https://dashboard.render.com/register)
- Репозиторий проекта [hexlet-cv](https://github.com/Hexlet/hexlet-cv) на GitHub, привязанный к Render

---

## Шаг 1. Создать PostgreSQL на Render

> **Важно:** Создайте базу данных один раз при первом развёртывании (при каждом деплое нового кода создавать её заново не нужно). Сделайте это до настройки переменных окружения — данные подключения понадобятся для Web Service.

1. Войдите в [Dashboard Render](https://dashboard.render.com).
2. Нажмите **New** → **PostgreSQL**.
3. Заполните (пример для дальнейшего использования в переменных окружения):
   - **Name** — `hexlet-cv-db`
   - **Database** — `hexlet_cv`
   - **User** — `hexlet_cv_user`
   - **Region** — Frankfurt (или ближайший к пользователям)
4. Нажмите **Create Database**.
5. Дождитесь статуса **Available**.
6. В карточке базы откройте вкладку **Info** и скопируйте:
   - **Internal Database URL** (для сервиса на Render) или **External Database URL** (если подключаетесь извне).

Формат URL:  
`postgresql://USER:PASSWORD@HOST/DATABASE?sslmode=require`

---

## Шаг 2. Создать Web Service (Docker)

Данные для переменных окружения (шаг 3) возьмите из карточки PostgreSQL, созданной в шаге 1. Если использовали примеры выше — `USERNAME` = `hexlet_cv_user`, `DATABASE` = `hexlet_cv`, `HOST` — из **Info** в карточке БД.

1. В Dashboard нажмите **New** → **Web Service**.
2. Подключите репозиторий:
   - Если ещё не подключён — **Connect account** (GitHub) и выберите репозиторий `hexlet-cv`.
   - Выберите репозиторий и нажмите **Connect**.
3. Настройки сервиса:
   - **Name** — например, `hexlet-cv`.
   - **Region** — тот же, что у базы (например, Frankfurt).
   - **Branch** — ветка для деплоя (обычно `main` или `master`).
   - **Runtime** — **Docker**.
   - **Dockerfile Path** — оставьте `./Dockerfile` (если Dockerfile в корне).
   - **Instance Type** — обычно выбирают **Free** (для начала достаточно).

---

## Шаг 3. Переменные окружения

В разделе **Environment** добавьте переменные для Web Service:

| Key                     | Пример значения                                                                      | Описание                          |
| :---------------------- |:-------------------------------------------------------------------------------------| :-------------------------------- |
| `SPRING_PROFILES_ACTIVE` | `prod`                                                                               | Включение продакшен-конфигурации  |
| `JDBC_DATABASE_URL`     | `jdbc:postgresql://{Hostname}:{Port}/{Database}?password={Password}&user={Username}` | Полный JDBC URL (подставьте HOST, PASSWORD и USER из карточки БД) |
| `USERNAME`              | `hexlet_cv_user`                                                                     | Из карточки PostgreSQL (см. шаг 1) |
| `PASSWORD`              | *ваш пароль из шага 1*                                                               | Из карточки PostgreSQL            |
| `DATABASE`              | `hexlet_cv_db`                                                                       | Из карточки PostgreSQL (см. шаг 1) |
| `HOST`                  | `dpg-xxx123-a`                                                                       | Internal hostname из карточки БД  |
| `DB_PORT`               | `5432`                                                                               | Порт PostgreSQL                   |

---

## Шаг 4. Деплой

1. Проверьте, что все переменные окружения сохранены.
2. Нажмите **Create Web Service**.
3. Render соберёт образ по Dockerfile и запустит контейнер. Первый деплой может занять несколько минут.
4. **Критерии успешного деплоя:**
   - В карточке Web Service в Dashboard статус **Live**.
   - По ссылке вида `https://…onrender.com` в браузере открывается сайт (главная страница приложения).

---

## Автодеплой из Git

- При пуше в выбранную ветку (например, `main`) Render по умолчанию запускает новый деплой.
- Отключить можно в настройках сервиса: **Settings** → **Build & Deploy** → **Auto-Deploy** → No.

---

## Опционально: render.yaml (Blueprint)

Можно описать сервис и БД в одном файле и развернуть через **Blueprint**:

1. В корне репозитория создайте `render.yaml` (или `render.yml`).
2. В Dashboard: **New** → **Blueprint** → выберите репозиторий [hexlet-cv](https://github.com/Hexlet/hexlet-cv); Render подхватит `render.yaml`.

Пример `render.yaml` для hexlet-cv (значения соответствуют шагу 1):

```yaml
databases:
  - name: hexlet-cv-db
    databaseName: hexlet_cv
    user: hexlet_cv_user
    plan: free
    region: frankfurt

services:
  - type: web
    name: hexlet-cv
    runtime: docker
    plan: free
    region: frankfurt
    repo: https://github.com/Hexlet/hexlet-cv
    branch: main
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: USERNAME
        fromDatabase:
          name: hexlet-cv-db
          property: user
      - key: PASSWORD
        fromDatabase:
          name: hexlet-cv-db
          property: password
      - key: DATABASE
        fromDatabase:
          name: hexlet-cv-db
          property: database
      # JDBC_DATABASE_URL — добавьте вручную в Dashboard (см. примечание ниже)
      - key: JDBC_DATABASE_URL
        sync: false
```

> **Важно:** `JDBC_DATABASE_URL` Render не задаёт автоматически (connectionString в формате `postgresql://...`, а Spring Boot ждёт `jdbc:postgresql://...`). Добавьте эту переменную вручную в Dashboard. Формат: `jdbc:postgresql://HOST:5432/DATABASE?password=PASSWORD&user=USER` — подставьте значения из карточки БД (Info).

Схема и свойства уточняйте в [документации Render](https://render.com/docs/blueprint-spec).

---
