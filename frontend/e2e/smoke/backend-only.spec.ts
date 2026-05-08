import { expect, test } from '@playwright/test'
import {
  assertAppRendered,
  assertNoCriticalClientErrors,
  attachPageDiagnostics,
  readHtml,
} from './helpers'

test('backend-only mode serves production-like app via backend origin', async ({
  page,
}) => {
  const diagnostics = attachPageDiagnostics(page)

  const response = await page.goto('/', { waitUntil: 'domcontentloaded' })
  const html = await readHtml(response)

  expect(response?.status()).toBe(200)
  expect(new URL(response!.url()).origin).toBe('http://127.0.0.1:8080')
  expect(html).not.toContain('@PageObject@')
  expect(html).toContain('data-page=')
  expect(html).toContain('/assets/')

  await assertAppRendered(page)
  await page.waitForTimeout(1_000)
  await assertNoCriticalClientErrors(diagnostics)

  const requestsToVite = diagnostics.requests.filter(
    (url) => url.includes('127.0.0.1:5173') || url.includes('localhost:5173'),
  )
  expect(requestsToVite).toEqual([])

  const assetRequests = diagnostics.requests.filter((url) =>
    url.includes('/assets/'),
  )
  expect(assetRequests.length).toBeGreaterThan(0)
  expect(
    assetRequests.every((url) =>
      url.startsWith('http://127.0.0.1:8080/assets/'),
    ),
  ).toBeTruthy()

  const assetResponses = diagnostics.responses.filter((responseItem) =>
    responseItem.url.includes('/assets/'),
  )
  expect(assetResponses.length).toBeGreaterThan(0)
  expect(
    assetResponses.every((responseItem) => responseItem.status === 200),
  ).toBeTruthy()
})
