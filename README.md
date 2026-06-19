# hexlet-cv

## Contents

- [Deploy on Render](docs/RENDER_DEPLOY.md)

## Description

The project - online service to create and publish CV. It is oriented on IT-specialists.
The platform will help users to create professional CV promptly and get link of it to send employers.

use [ https://github.com/Inertia4J/inertia4j ] 

## Local Run

### Production-like backend-only mode

`make run`

This command builds the frontend with Vite and then starts the Java backend. At runtime the Vite dev server is not used: Spring Boot reads `frontend/dist/index.html` through the runtime classpath and serves static files from `frontend/dist`.

Open `http://localhost:8080`.

### Frontend dev mode

If you need the Vite dev server separately:

1. Start backend in one terminal: `make run`
2. Start Vite in another terminal: `make -C frontend dev`
3. Open `http://localhost:5173`

In this mode Vite serves the HTML document itself and injects a mock Inertia initial page through a dev-only HTML transform. The backend is used for API/Inertia requests, and the root HTML document is not proxied to the backend.

### How it works

- `make run` first runs `npm run build` in `frontend`, then starts `./gradlew run`
- `frontend/dist` is added to backend runtime classpath, and backend Inertia config reads the template path from `inertia.template-path`
- Files from `frontend/dist`, including `frontend/dist/assets`, are exposed by Spring Boot from the backend origin through `spring.web.resources.static-locations`
- `frontend/index.html` keeps the literal `@PageObject@`; in `vite serve` it is replaced by a mock page, and in `vite build` it stays untouched for backend template rendering

If you run `./gradlew run` directly, `frontend/dist` must already exist and be up to date.

### Verification

After startup via `make run`:

- open the application through the backend URL, usually `http://localhost:8080`
- in DevTools Network, HTML must come from the backend origin
- JS/CSS requests must load from the same backend origin, for example `/assets/...`
- there must be no requests to `http://localhost:5173`
 
