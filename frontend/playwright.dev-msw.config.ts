import { createSmokeConfig, createWebServer } from './playwright.shared'

export default createSmokeConfig({
  baseURL: 'http://127.0.0.1:5173',
  testMatch: [
    /[/\\]e2e[/\\]smoke[/\\]dev-msw\.spec\.ts$/,
    /[/\\]e2e[/\\]pages[/\\].*\.msw\.spec\.ts$/,
  ],
  webServer: createWebServer(
    'VITE_MSW=true npm run dev -- --host 127.0.0.1 --strictPort',
    5173,
  ),
})
