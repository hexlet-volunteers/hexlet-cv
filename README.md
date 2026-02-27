# hexlet-cv

**Description:**

The project - online service to create and publish CV. It is oriented on IT-specialists.
The platform will help users to create professional CV promptly and get link of it to send employers.

## Оглавление

- [hexlet-cv](#hexlet-cv)
- [Git](#git)
- [Frontend](#frontend)
  - [Get started](#get-started)
    - [Install](#install)
    - [Build](#build)
    - [Development](#development)
  - [Соглашения по разработке frontend](#соглашения-по-разработке-frontend)
- [Backend](#backend)
  - [Соглашения по разработке backend](#соглашения-по-разработке-backend)

# hexlet-cv

use [ https://github.com/Inertia4J/inertia4j ]

# Git

- 1 МР - 1 коммит
- Комментарии коммита пишем на русском ( пример: "[feat-7]: Создан новый футер." ).
    * Справочно [Соглашение о коммитах](https://www.conventionalcommits.org/ru/v1.0.0/)

    ```bash
    git add . && git commit -m "%useful_commit_message%"
    git push --set-upstream origin %your_branch_name%
    ```
- Как исправлять конфликты на мердж реквесте в ветку main основного репозитория
1. В своем репозитории выполнить синхронизацию ветки main с основным репозиторием (кнопка Sync fork)
2. Выполнить следующие команды на source-ветке
    ```
    git fetch origin main
    git rebase origin/main
    ```
3. Если есть конфликты, необходимо их исправить и выполнить команду, и так продолжаем пока не будет исправлены все конфликты
    ```
    git rebase --continue
    ```
4. Проверить что приложение успешно компилится и запускается
5. Запушить изменения
    ```
    git push --force
    ```

# Frontend

Стек: React, Mantine, Inertia, TS, Vite.

## Get started

### Install

1. Установить NodeJS 20.19 or above (https://nodejs.org/en/download)
2. Проверить версию Node.js `node -v`
3. `npm ci`
4. Сгенерировать файл сервис воркера для работы моков `npx msw init ./public --save`
5. Создать файл .env.local и прописать в нем `VITE_MSW=true`

### Build

1. `npm run build`

### Development

1. `npm run dev`
2. Открыть http://localhost:5173/

### Deploy Preview (Vite + MSW)

1. Зайдите в свой аккаунт на https://render.com и нажмите "New +", далее выберите "Web Service"
2. Выберите пункт "Build and deploy from a Git repository"
3. Если ваш аккаунт на Github привязан, вы сможете выбрать проект из списка репозиториев. Выберите нужный репозиторий с приложением.
4. Заполните настройки приложения:
```
name - hexlet-cv-preview
language - Node
branch - выбрать ветку для деплоя
region - любой
root directory - frontend
instance type - free
build command - npm ci && npm run build
start command - npm run preview
```
5. Добавьте Environment Variables
```
DEV - true
VITE_MSW - true
```
6. Нажмите "Deploy Web Service". Через некоторое время приложение соберется и запустится.

## Соглашения по разработке frontend

- `[JS]` Если у функции больше 3 аргументов или есть опциональные параметры, используем один объект-аргумент params/options.

```
fn(prop1, prop2) // ok
fn({prop1, prop2, prop5}) // ok, вместо fn(prop1, prop2, null, null, prop5)
fn(param, {prop1, prop2, prop5}) // ок, например, fetch(url: string, options: Options)
```

- `[React]` Для map в JSX если рендерим компонент с множестровом пропов или там есть логика внутри map, то делаем как items.map(renderItem). Как это влияет на практике - с одной стороны без return короче и проще, но ровно до момента отладки/дебага в дев тулз или банально поставить console.log, вариант с renderItem выглядит компромиссным.

```
const renderItem = (props) => {
    const {prop1} = props

    if (prop1) return null

    return <Item {...props} />
}

return (
    <div>
        {items.map(renderItem)}
    </div>
)
```

- `[React]` Распаковываем пропсы внутри компонента. Допускается распаковка в аргументах для компонентов с 1–2 пропсами или простых обёрток.

```
const Component: React.FC<TProps> = (props) => {const {prop1, prop2, prop3} = props}
```

- `[TS]` для типизации API DTO используем type, для props публичных UI-компонентов - interface.

```{
  "overrides": [
    {
      "files": ["src/**/ui/**/*.{ts,tsx}", "src/**/*.props.ts"],
      "rules": {
        "@typescript-eslint/consistent-type-definitions": ["error", "interface"]
      }
    },
    {
      "files": ["src/**/api/**/*.{ts,tsx}", "src/**/*.dto.ts"],
      "rules": {
        "@typescript-eslint/consistent-type-definitions": ["error", "type"]
      }
    }
  ]
}
```

- `[Style]` Новые prop в интерфейс или type или когда прокидываем уже в JSX в компоненте вставляем по алфавиту. Как это влияет на практике, один разработчик добавил новый проп просто в конец, другой разработчик - тоже в конец - результат конфликт на мердже, требующий ручной правки.

```
{
    "rules": {
        "react/jsx-sort-props": [
            "error",
            {
                "callbacksLast": true,
                "reservedFirst": true,
                "shorthandFirst": true
            }
        ]
    }
}
```

- `[UI/DS]` Для рендера Card используем компонент DSCard.
- `[UI/DS]` Для рендера Text используем компонент DSText.

```
{
    "rules": {
        "no-restricted-imports": [
            "error", 
            {
                "paths": [{
                    "name": "@mantine/core",
                    "importNames": ["Card", "Text"],
                    "message": "Используй DSCard/DSText"
                }]
            }
        ],
    }
}
```

# Backend

## Соглашения по разработке backend
