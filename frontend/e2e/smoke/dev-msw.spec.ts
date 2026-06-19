import { expect, test } from '@playwright/test'
import {
  assertAppRendered,
  assertNoCriticalClientErrors,
  attachPageDiagnostics,
  readHtml,
} from './helpers'

test('vite dev mode with MSW works standalone without backend', async ({
  page,
}) => {
  const diagnostics = attachPageDiagnostics(page)

  const response = await page.goto('/', { waitUntil: 'domcontentloaded' })
  const html = await readHtml(response)

  expect(response?.status()).toBe(200)
  expect(new URL(response!.url()).origin).toBe('http://127.0.0.1:5173')
  expect(html).not.toContain('@PageObject@')

  await assertAppRendered(page)
  await page.waitForTimeout(1_000)
  await assertNoCriticalClientErrors(diagnostics)

  const backendRequests = diagnostics.requests.filter(
    (url) => url.includes('127.0.0.1:8080') || url.includes('localhost:8080'),
  )
  expect(backendRequests).toEqual([])
})
