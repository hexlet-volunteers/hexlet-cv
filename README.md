# Хекслет Карьера (hexlet-cv)

**Хекслет Карьера** — сервис, который ведёт IT-специалиста от отклика до оффера: агрегация вакансий с матчингом под профиль, резюме с AI-ревью и ATS-проверкой, пакет писем, kanban-трекер откликов, AI-собеседования и живые моки с наставниками, разбор отказов, зарплатный радар и рефералы выпускников.

> Проект развивается волонтёрами сообщества Хекслета. Сейчас идёт переход со старого продукта (онлайн-резюме) на новый дизайн «Хекслет Карьера» — см. [дорожную карту](#дорожная-карта).

## Стек

| Слой | Технологии |
|---|---|
| Backend | Java 24, Spring Boot, Spring Security (JWT-куки), JPA, Gradle |
| Frontend | React 18, TypeScript, Vite, Mantine, MSW (dev-моки) |
| Интеграция | Inertia.js (текущая) → миграция на REST + TanStack Query ([#1111](https://github.com/hexlet-volunteers/hexlet-cv/issues/1111)) |
| Контракт API | **Design First**: TypeSpec → OpenAPI → генерация BE (OpenAPI Generator) и FE-клиента ([эпик #1106](https://github.com/hexlet-volunteers/hexlet-cv/issues/1106)) |

## Структура репозитория

```
├── src/                  # Java/Spring backend
├── frontend/             # React frontend (Vite)
├── docs/
│   ├── design/           # дизайн «Хекслет Карьера»: скриншоты, прототипы, карта экранов
│   └── RENDER_DEPLOY.md  # деплой на Render
├── ruby-version/         # legacy-приложение на Rails (не развивается)
└── Makefile              # команды backend
```

## Быстрый старт

### Backend

Требуется Java 24 (в репо есть [mise](https://mise.jdx.dev/): `mise install`).

```bash
make setup   # gradle wrapper + build
make run     # запуск на http://localhost:8080
```

### Frontend

Требуется Node.js ≥ 20.19.

```bash
cd frontend
npm ci
npx msw init ./public --save     # сервис-воркер для dev-моков
echo "VITE_MSW=true" > .env.local
npm run dev                      # http://localhost:5173
```

## Проверки и тесты

```bash
# backend
make test     # тесты
make lint     # checkstyle
make report   # тесты + jacoco-отчёт

# frontend
cd frontend
npm run lint
npm run format
npm run build
```

## Как мы разрабатываем: Design First

Любое изменение API начинается с контракта, а не с кода:

1. Правим схемы **TypeSpec** (задача [#1107](https://github.com/hexlet-volunteers/hexlet-cv/issues/1107)).
2. Компилируем в **OpenAPI** ([#1108](https://github.com/hexlet-volunteers/hexlet-cv/issues/1108)).
3. Генерируем из OpenAPI:
   - **backend** — серверную обвязку целиком (интерфейсы/делегаты, DTO, валидация) через OpenAPI Generator ([#1109](https://github.com/hexlet-volunteers/hexlet-cv/issues/1109)); бизнес-логика пишется в делегатах/сервисах, сгенерированный слой руками не правится;
   - **frontend** — типизированный клиент + хуки TanStack Query ([#1110](https://github.com/hexlet-volunteers/hexlet-cv/issues/1110)).
4. Фронт и бэк раскатываются раздельно.

Подробности и конвенции — [#1112](https://github.com/hexlet-volunteers/hexlet-cv/issues/1112), CI контракта — [#1113](https://github.com/hexlet-volunteers/hexlet-cv/issues/1113).

## Дорожная карта

Работа разбита на майлстоуны (MVP-first) и эпики с детальными задачами:

| Майлстоун | Эпики |
|---|---|
| [M1 — Фундамент и запуск](https://github.com/hexlet-volunteers/hexlet-cv/milestone/4) | [Публичный сайт #1119](https://github.com/hexlet-volunteers/hexlet-cv/issues/1119) · [Аутентификация #1120](https://github.com/hexlet-volunteers/hexlet-cv/issues/1120) · [Оболочка и онбординг #1121](https://github.com/hexlet-volunteers/hexlet-cv/issues/1121) |
| [M2 — Вакансии и матчинг](https://github.com/hexlet-volunteers/hexlet-cv/milestone/5) | [Источники и парсеры #1122](https://github.com/hexlet-volunteers/hexlet-cv/issues/1122) · [Вакансии #1123](https://github.com/hexlet-volunteers/hexlet-cv/issues/1123) |
| [M3 — Трекер откликов](https://github.com/hexlet-volunteers/hexlet-cv/milestone/6) | [Трекер #1124](https://github.com/hexlet-volunteers/hexlet-cv/issues/1124) |
| [M4 — Резюме и письма](https://github.com/hexlet-volunteers/hexlet-cv/milestone/7) | [Резюме #1125](https://github.com/hexlet-volunteers/hexlet-cv/issues/1125) · [Письма #1126](https://github.com/hexlet-volunteers/hexlet-cv/issues/1126) |
| [M5 — Подготовка к собесам](https://github.com/hexlet-volunteers/hexlet-cv/milestone/8) | [AI-собес, моки, вопросы #1127](https://github.com/hexlet-volunteers/hexlet-cv/issues/1127) |
| [M6 — Аналитика и профиль](https://github.com/hexlet-volunteers/hexlet-cv/milestone/9) | [Аналитика #1128](https://github.com/hexlet-volunteers/hexlet-cv/issues/1128) · [Зарплаты и грейды #1129](https://github.com/hexlet-volunteers/hexlet-cv/issues/1129) · [Рефералы #1130](https://github.com/hexlet-volunteers/hexlet-cv/issues/1130) · [Профиль #1131](https://github.com/hexlet-volunteers/hexlet-cv/issues/1131) |
| [M7 — Админка](https://github.com/hexlet-volunteers/hexlet-cv/milestone/10) | [Админка #1132](https://github.com/hexlet-volunteers/hexlet-cv/issues/1132) |

Все задачи содержат юз-кейс, критерии приёмки и скриншот экрана из дизайна.

## Дизайн

Скриншоты всех экранов, интерактивные прототипы (`.dc.html` — откройте в браузере) и карта «экран → задача»: [docs/design/](docs/design/README.md).

## Как помочь

1. Выберите задачу в [открытых issues](https://github.com/hexlet-volunteers/hexlet-cv/issues) (начните с [M1](https://github.com/hexlet-volunteers/hexlet-cv/milestone/4)); задачи с меткой `Frontend`/`Backend` — по вашему профилю.
2. Отпишитесь в задаче, что берёте её в работу.
3. Форк → ветка → PR на `main` с ссылкой на задачу. Изменения API — только через TypeSpec (см. Design First).

## Деплой

Инструкция по развёртыванию на Render: [docs/RENDER_DEPLOY.md](docs/RENDER_DEPLOY.md).
