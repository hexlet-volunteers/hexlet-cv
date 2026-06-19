import { expect, type Page, type Response } from '@playwright/test'

const allowedConsoleErrorPatterns: RegExp[] = [
  // Keep this list explicit. Add only documented non-fatal errors here.
]

export type PageDiagnostics = ReturnType<typeof attachPageDiagnostics>

export function attachPageDiagnostics(page: Page) {
  const consoleErrors: string[] = []
  const pageErrors: string[] = []
  const requests: string[] = []
  const responses: Array<{ status: number; url: string }> = []

  page.on('console', (message) => {
    if (message.type() !== 'error') {
      return
    }

    const text = message.text()
    const isAllowed = allowedConsoleErrorPatterns.some((pattern) =>
      pattern.test(text),
    )

    if (!isAllowed) {
      consoleErrors.push(text)
    }
  })

  page.on('pageerror', (error) => {
    pageErrors.push(String(error))
  })

  page.on('request', (request) => {
    requests.push(request.url())
  })

  page.on('response', (response) => {
    responses.push({
      status: response.status(),
      url: response.url(),
    })
  })

  return {
    consoleErrors,
    pageErrors,
    requests,
    responses,
  }
}

export async function readHtml(response: Response | null): Promise<string> {
  expect(
    response,
    'Expected the main document response to exist',
  ).not.toBeNull()
  return response!.text()
}

export async function assertNoCriticalClientErrors(
  diagnostics: PageDiagnostics,
) {
  expect(
    diagnostics.pageErrors,
    `Unexpected page errors:\n${diagnostics.pageErrors.join('\n')}`,
  ).toEqual([])
  expect(
    diagnostics.consoleErrors,
    `Unexpected console errors:\n${diagnostics.consoleErrors.join('\n')}`,
  ).toEqual([])
}

export async function assertAppRendered(page: Page) {
  await expect
    .poll(
      async () =>
        page.locator('#app').evaluate((element) => {
          const textLength = element.textContent?.trim().length ?? 0
          return textLength + element.childElementCount
        }),
      { message: '#app should render some visible content' },
    )
    .toBeGreaterThan(0)
}
