# hexlet-cv

## Contents

- [Deploy on Render](docs/RENDER_DEPLOY.md)

## Description

The project - online service to create and publish CV. It is oriented on IT-specialists.
The platform will help users to create professional CV promptly and get link of it to send employers.

use [ https://github.com/Inertia4J/inertia4j ] 

## Local environment

The recommended way to manage local runtime versions is `asdf`.

The project uses:

- Node.js `20.19.5` for the frontend
- Java 24 for the backend

Runtime versions are defined in `.tool-versions`.

Install the required plugins once:

```bash
asdf plugin add nodejs
asdf plugin add java
```

Install the project versions:

```bash
asdf install
```

Verify the active versions:

```bash
node --version
npm --version
java --version
asdf current
```

Gradle is managed by the project Gradle Wrapper, so do not install or pin Gradle through `asdf`.

```bash
./gradlew --version
```

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

### Docker

Docker builds are self-contained and do not use local `asdf`, Node.js, Java, or Gradle installations.

The Makefile reads versions from `.tool-versions` and passes them to Docker as build args.

Direct `docker build` also works through Dockerfile defaults, but these defaults should be kept in sync with `.tool-versions`.

```bash
make docker-build
make docker-run
```

### Smoke Tests

Before the first local e2e run, install Chromium for Playwright:

```bash
cd frontend
npm install
npm run test:e2e:install
```

Or via Makefile:

```bash
cd frontend
make test-e2e-install
```

Smoke tests verify local run-mode invariants: who serves the HTML document, whether assets load, whether `@PageObject@` is replaced, and whether the page renders without critical client errors.

Smoke specs live in `frontend/e2e/smoke`.
Strict smoke helpers live in `frontend/e2e/smoke/helpers.ts`. They track page errors, console errors, and request/response activity for run-mode diagnostics.

GitHub Actions runs e2e in a separate workflow: `E2E`.

```bash
cd frontend
make test-e2e-backend-only
make test-e2e-dev-backend
make test-e2e-dev-msw
make test-e2e-modes
```

### E2E Strategy

#### Product e2e with MSW

This is the default mode for future frontend product scenarios.

- Put specs in `frontend/e2e/pages`
- Name files as `*.msw.spec.ts`
- Run them through `cd frontend && make test-e2e-dev-msw`
- Use MSW for stable data setup and edge cases
- Do not require a running backend
- Do not use `frontend/e2e/smoke/helpers.ts` as the default wrapper for product tests
- Prefer user-facing assertions through roles, text, URL, and visible state
- Add separate neutral helpers or fixtures later if product scenarios need them

Example:

```ts
import { expect, test } from '@playwright/test'

test('example page flow', async ({ page }) => {
  await page.goto('/')

  await expect(page.getByRole('heading', { name: /home/i })).toBeVisible()
  await expect(page.getByText(/welcome/i)).toBeVisible()
})
```

For example, a future spec can live at `frontend/e2e/pages/example.msw.spec.ts`.

#### Backend integration e2e

Use this mode only when the test must verify the real backend contract: Inertia payloads, shared props, form submits, backend validation, or server-driven page data.

- Put specs in `frontend/e2e/integration`
- Name files as `*.backend.spec.ts`
- Run them through `cd frontend && make test-e2e-dev-backend`
- Keep `backend-only` focused mainly on smoke verification of the production-like local run

If a test fails with an error like `Executable doesn't exist at .../ms-playwright/...`, run:

```bash
cd frontend
npm run test:e2e:install
```

Backend, root, and frontend CI workflows do not run Playwright e2e, so faster checks stay separate from the smoke suite.

### Verification

After startup via `make run`:

- open the application through the backend URL, usually `http://localhost:8080`
- in DevTools Network, HTML must come from the backend origin
- JS/CSS requests must load from the same backend origin, for example `/assets/...`
- there must be no requests to `http://localhost:5173`
 
