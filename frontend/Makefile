install:
	npm ci

build:
	npm run build
	
dev:
	npm run dev

preview:
	npm run preview

lint:
	npm run lint

test-e2e-install:
	npm run test:e2e:install

test-e2e-backend-only:
	npm run test:e2e:backend-only

test-e2e-dev-backend:
	npm run test:e2e:dev-backend

test-e2e-dev-msw:
	npm run test:e2e:dev-msw

test-e2e-modes:
	npm run test:e2e:modes

check: lint build

ci: install check

.PHONY: install dev preview lint build check ci test-e2e-install test-e2e-backend-only test-e2e-dev-backend test-e2e-dev-msw test-e2e-modes
