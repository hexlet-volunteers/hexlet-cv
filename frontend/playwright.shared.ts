import {
  defineConfig,
  devices,
  type PlaywrightTestConfig,
} from '@playwright/test'

type WebServerConfig = NonNullable<PlaywrightTestConfig['webServer']>

type SmokeConfigOptions = {
  baseURL: string
  testMatch: PlaywrightTestConfig['testMatch']
  webServer: WebServerConfig
}

export function createSmokeConfig({
  baseURL,
  testMatch,
  webServer,
}: SmokeConfigOptions): PlaywrightTestConfig {
  return defineConfig({
    testDir: './e2e',
    testMatch,
    fullyParallel: false,
    forbidOnly: !!process.env.CI,
    retries: process.env.CI ? 1 : 0,
    workers: 1,
    timeout: 60_000,
    expect: {
      timeout: 10_000,
    },
    reporter: process.env.CI ? [['github'], ['list']] : 'list',
    use: {
      baseURL,
      trace: 'retain-on-failure',
      screenshot: 'only-on-failure',
      video: 'retain-on-failure',
    },
    projects: [
      {
        name: 'chromium',
        use: {
          ...devices['Desktop Chrome'],
          browserName: 'chromium',
        },
      },
    ],
    webServer,
  })
}

export function createWebServer(
  command: string,
  port: number,
): Exclude<WebServerConfig, WebServerConfig[]> {
  return {
    command,
    url: `http://127.0.0.1:${port}`,
    reuseExistingServer: !process.env.CI,
    timeout: 300_000,
  }
}
