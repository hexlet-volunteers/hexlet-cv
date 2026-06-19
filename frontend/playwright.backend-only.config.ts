import { createSmokeConfig, createWebServer } from './playwright.shared'

export default createSmokeConfig({
  baseURL: 'http://127.0.0.1:8080',
  testMatch: /[/\\]e2e[/\\]smoke[/\\]backend-only\.spec\.ts$/,
  webServer: createWebServer('cd .. && ./gradlew run', 8080),
})
