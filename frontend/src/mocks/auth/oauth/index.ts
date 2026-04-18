import { HttpResponse } from 'msw'
import { defineGet } from '@mocks/msw/define'
import { mockDbUsers } from '@mocks/state/mockDb'
import { issueTokens } from '@mocks/state/tokenStore'
import { issueOAuthState, consumeOAuthState } from '@mocks/state/oauthStore'

const LOCALES = new Set(['ru', 'en'])

const getLocaleFromReferer = (request: Request) => {
  const ref = request.headers.get('referer')
  if (!ref) return null

  try {
    const first = new URL(ref).pathname.split('/').filter(Boolean)[0]
    return first && LOCALES.has(first) ? first : null
  } catch {
    return null
  }
}

const safeNext = (raw: string | null, locale: string) => {
  const fallback = `/${locale}/account`
  if (!raw) return fallback

  // Разрешаем только относительные пути
  if (!raw.startsWith('/')) return fallback
  if (raw.startsWith('//')) return fallback

  return raw
}

const pickProviderFromPath = (pathNoLocale: string, prefix: string) => {
  if (!pathNoLocale.startsWith(prefix)) return null
  const rest = pathNoLocale.slice(prefix.length)
  const seg = rest.split('/').filter(Boolean)[0]
  return seg || null
}

// 1) старт OAuth: /auth/oauth/:provider/start
const oauthStart = defineGet('*/auth/oauth/*/start', (ctx, request) => {
  const provider = pickProviderFromPath(ctx.pathNoLocale, '/auth/oauth/')
  if (!provider) {
    return ctx.inertiaPage('Users/SignIn/Index', { flash: {}, errors: {} })
  }

  const locale = ctx.hasLocale
    ? ctx.locale
    : (getLocaleFromReferer(request) ?? ctx.locale)

  const url = new URL(request.url)
  const next = safeNext(url.searchParams.get('next'), locale)

  const state = issueOAuthState({ provider, locale, next })

  const dest = `/${locale}/oauth/mock/${provider}?state=${encodeURIComponent(state)}&next=${encodeURIComponent(next)}`

  return new HttpResponse(null, {
    status: 303,
    headers: { location: dest },
  })
})

// 2) mock consent page: /oauth/mock/:provider
const oauthMockPage = defineGet('*/oauth/mock/*', (ctx, request) => {
  const provider = pickProviderFromPath(ctx.pathNoLocale, '/oauth/mock/')
  if (!provider) {
    return ctx.inertiaPage('Users/SignIn/Index', { flash: {}, errors: {} })
  }

  const url = new URL(request.url)
  const state = url.searchParams.get('state') ?? ''
  const next = safeNext(url.searchParams.get('next'), ctx.locale)

  return ctx.inertiaPage(
    'Auth/OAuthMock/Index',
    {
      flash: {},
      errors: {},
      provider,
      state,
      next,
      locale: ctx.locale,
      auth: { user: ctx.user },
    },
    200,
    `/${ctx.locale}/oauth/mock/${provider}`,
  )
})

// 3) callback: /auth/oauth/:provider/callback
const oauthCallback = defineGet('*/auth/oauth/*/callback', (ctx, request) => {
  const provider = pickProviderFromPath(ctx.pathNoLocale, '/auth/oauth/')
  if (!provider) {
    return ctx.inertiaPage(
      'Users/SignIn/Index',
      { flash: {}, errors: {} },
      200,
      `/${ctx.locale}/users/sign_in`,
    )
  }

  const url = new URL(request.url)
  const state = url.searchParams.get('state') ?? ''
  const code = url.searchParams.get('code') ?? 'mock'

  const rec = consumeOAuthState(state, provider)
  if (!rec) {
    return ctx.inertiaPage(
      'Users/SignIn/Index',
      {
        flash: { error: 'OAuth-сессия устарела. Повторите вход.' },
        errors: {},
      },
      200,
      `/${ctx.locale}/users/sign_in`,
    )
  }

  const email = `${provider}+${code}@gmail.com`.toLowerCase()
  const existing = mockDbUsers.findByEmail(email)
  const user = existing ?? mockDbUsers.createUser(email, 'oauth')

  const { accessToken, refreshToken } = issueTokens(String(user.id))
  ctx.setCookie('access_token', accessToken, { maxAge: 10 * 60 })
  ctx.setCookie('refresh_token', refreshToken, { maxAge: 14 * 24 * 60 * 60 })

  return ctx.redirect('/{locale}/account/purchase')
})

export const handlers = [oauthStart, oauthMockPage, oauthCallback]
