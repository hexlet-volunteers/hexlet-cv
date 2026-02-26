/* eslint-disable indent */
import { HttpResponse } from 'msw'
import { parseCookies, serializeCookies, type CookieOptions } from '@mocks/helpers/cookies'

const DEFAULT_LOCALE = 'ru'
const LOCALES = new Set(['ru', 'en'])

const normalizePath = (p: string) => (p.startsWith('/') ? p : `/${p}`)

export type MswCtx = ReturnType<typeof createCtx>

export function createCtx(request: Request, opts?: { cookies?: Record<string, string> }) {
  const url = new URL(request.url)

  const cookies = opts?.cookies ?? parseCookies(request.headers.get('cookie'))

  // --- locale + pathNoLocale ---
  const parts = url.pathname.split('/').filter(Boolean)
  const first = parts[0]
  const hasLocale = Boolean(first && LOCALES.has(first))
  const locale = hasLocale ? first : DEFAULT_LOCALE

  const pathNoLocale = normalizePath((hasLocale ? parts.slice(1) : parts).join('/'))

  const canonicalPath = `/${locale}${pathNoLocale}`

  const setCookieQueue: string[] = []

  const pushSetCookie = (setCookieValue: string) => {
    setCookieQueue.push(setCookieValue)
  }

  const isHttps = url.protocol === 'https:'
  const hostname = url.hostname
  const envDomain = import.meta.env?.VITE_MSW_COOKIE_DOMAIN as string | undefined
  const cookieDomain
    = envDomain
      ?? (hostname === 'hexlet.io' || hostname.endsWith('.hexlet.io')
        ? '.hexlet.io'
        : hostname === 'hexlet.ru' || hostname.endsWith('.hexlet.ru')
          ? '.hexlet.ru'
          : undefined)
  const baseCookieOpts: CookieOptions = {
    path: '/',
    httpOnly: true,
    secure: isHttps,
    sameSite: 'lax',
    domain: cookieDomain,
  }

  const setCookie = (name: string, value: string, opts: CookieOptions = {}) => {
    pushSetCookie(serializeCookies(name, value, { ...baseCookieOpts,
      ...opts }))
  }

  const clearCookie = (name: string, opts: CookieOptions = {}) => {
    pushSetCookie(serializeCookies(name, '', { ...baseCookieOpts,
      ...opts,
      maxAge: 0 }))
  }

  const inertiaPage = (
    component: string,
    props: Record<string, unknown>,
    status = 200,
    urlOverride?: string
  ) => {
    const queuedCookies = setCookieQueue.splice(0)

    const payload = {
      component,
      props,
      url: (urlOverride ?? canonicalPath) + url.search,
      version: null,
    }

    const headers: Array<[string, string]> = [
      ['x-inertia', 'true'],
      ['content-type', 'application/json; charset=utf-8'],
      ['vary', 'Accept'],
      ...queuedCookies.map(c => ['set-cookie', c] as [string, string]),
    ]

    return new HttpResponse(JSON.stringify(payload), { status,
      headers })
  }

  return {
    request,
    url,
    locale,
    hasLocale,
    pathNoLocale,
    canonicalPath,
    cookies,
    setCookie,
    clearCookie,
    inertiaPage,
  }
}
