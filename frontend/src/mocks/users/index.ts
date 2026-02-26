import { authStore } from '@mocks/state/authStore'
import { mockDbUsers } from '@mocks/state/mockDb'
import { defineGet, definePost } from '@mocks/msw/define'
import { issueTokens, revoke } from '@mocks/state/tokenStore'
import { accountMenu } from '@mocks/account'
import { accountPurchases } from '@mocks/account/purchase'
import type { createCtx } from '@mocks/msw/createCtx'
import { clearOAuthStates } from '@mocks/state/oauthStore'

const baseProps = () => ({
  flash: {},
  errors: {},
  auth: { user: authStore.getUser() },
})

const signUpHandler = (ctx: ReturnType<typeof createCtx>, _req: Request, body: { email: string, password: string }) => {
  const email = String(body.email ?? '')
  const password = String(body.password ?? '')

  if (mockDbUsers.findByEmail(email)) {
    return ctx.inertiaPage('Users/SignUp/Index', { ...baseProps(),
      errors: { email: 'Email уже занят' } }, 422)
  }

  const user = mockDbUsers.createUser(email, password)
  authStore.setUser({ id: user.id,
    email: user.email,
    roles: user.roles })

  const { accessToken, refreshToken } = issueTokens(String(user.id))
  ctx.setCookie('access_token', accessToken, { maxAge: 10 * 60 })
  ctx.setCookie('refresh_token', refreshToken, { maxAge: 14 * 24 * 60 * 60 })

  return ctx.inertiaPage('Account/Purchase/Index', { ...baseProps(),
    menu: accountMenu,
    purchases: accountPurchases }, 200, `/${ctx.locale}/account`)
}

export const handlers = [
  defineGet('*/users/sign_up', ctx =>
    ctx.inertiaPage('Users/SignUp/Index', baseProps())
  ),
  defineGet('*/users/sign_in', ctx =>
    ctx.inertiaPage('Users/SignIn/Index', baseProps())
  ),
  definePost<{ email: string, password: string }>('*/users/sign_in', (ctx, _req, body) => {
    const email = String(body.email ?? '')
    const password = String(body.password ?? '')

    const user = mockDbUsers.findByEmail(email)
    if (!user || user.password !== password) {
      return ctx.inertiaPage('Users/SignIn/Index', { ...baseProps(),
        errors: { email: 'email.invalid' },
      }, 422)
    }

    authStore.setUser({ id: user.id,
      email: user.email,
      roles: user.roles })

    const { accessToken, refreshToken } = issueTokens(String(user.id))
    ctx.setCookie('access_token', accessToken, { maxAge: 10 * 60 })
    ctx.setCookie('refresh_token', refreshToken, { maxAge: 14 * 24 * 60 * 60 })

    return ctx.inertiaPage('Account/Purchase/Index', { ...baseProps(),
      menu: accountMenu,
      purchases: accountPurchases }, 200, `/${ctx.locale}/account`)
  }),
  definePost<{ email: string, password: string }>('*/users/sign_up', (ctx, _req, body) => {
    if (mockDbUsers.findByEmail(body.email)) {
      return ctx.inertiaPage('Users/SignUp/Index', { flash: {},
        errors: { email: 'Email уже занят' } }, 422)
    }

    const user = mockDbUsers.createUser(body.email, body.password)
    authStore.setUser({ id: user.id,
      email: user.email,
      roles: user.roles })

    const { accessToken, refreshToken } = issueTokens(String(user.id))
    ctx.setCookie('access_token', accessToken, { maxAge: 10 * 60 })
    ctx.setCookie('refresh_token', refreshToken, { maxAge: 14 * 24 * 60 * 60 })

    return ctx.inertiaPage('Account/Purchase/Index', { auth: { user: authStore.getUser() },
      menu: accountMenu,
      purchases: accountPurchases }, 200, `/${ctx.locale}/account`)
  }),
  definePost<{ email: string, password: string }>('*/users/sign_up', signUpHandler),
  definePost<{ email: string, password: string }>('*/users', signUpHandler),
  definePost<{ email: string, password: string }>('*/users/sign_out', (ctx) => {
    revoke(ctx.cookies.access_token, ctx.cookies.refresh_token)

    clearOAuthStates()

    ctx.clearCookie('access_token')
    ctx.clearCookie('refresh_token')

    authStore.reset()

    return ctx.inertiaPage('Users/SignIn/Index', { flash: { success: 'Вы вышли из аккаунта' },
      errors: {},
      auth: { user: null } }, 200, `/${ctx.locale}/users/sign_in`)
  }),
]
