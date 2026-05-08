import { createSmokeConfig, createWebServer } from './playwright.shared'

export default createSmokeConfig({
  baseURL: 'http://127.0.0.1:5173',
  testMatch: [
    /[/\\]e2e[/\\]smoke[/\\]dev-backend\.spec\.ts$/,
    /[/\\]e2e[/\\]integration[/\\].*\.backend\.spec\.ts$/,
  ],
  webServer: [
    createWebServer('cd .. && ./gradlew run', 8080),
    createWebServer(
      'VITE_MSW=false npm run dev -- --host 127.0.0.1 --strictPort',
      5173,
    ),
  ],
})
