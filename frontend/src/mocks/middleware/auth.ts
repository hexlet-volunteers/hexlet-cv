import type { Middleware } from '@mocks/msw/pipeline'
import { authStore } from '@mocks/state/authStore'
import { mockDbUsers } from '@mocks/state/mockDb'
import { rotateAccess, validateAccess } from '@mocks/state/tokenStore'

const isProtected = (pathNoLocale: string) =>
  /^\/account(?:\/|$)/.test(pathNoLocale)
  || /^\/admin(?:\/|$)/.test(pathNoLocale)

export const authMiddleware: Middleware = (ctx) => {
  const accessToken = ctx.cookies.access_token
  const refreshToken = ctx.cookies.refresh_token

  const access = validateAccess(accessToken)
  if (access) {
    if (!authStore.isAuthed()) {
      const user = mockDbUsers.findById(Number(access.userId))
      if (user) authStore.setUser({ id: user.id,
        email: user.email,
        roles: user.roles })
    }
    return
  }

  const rotated = rotateAccess(refreshToken)
  if (rotated) {
    ctx.setCookie('access_token', rotated.accessToken, { maxAge: 10 * 60 })

    const user = mockDbUsers.findById(Number(rotated.userId))
    if (user) authStore.setUser({ id: user.id,
      email: user.email,
      roles: user.roles })

    return
  }

  if (authStore.isAuthed()) authStore.reset()

  ctx.clearCookie('access_token')
  ctx.clearCookie('refresh_token')

  if (!isProtected(ctx.pathNoLocale)) return

  return ctx.inertiaPage('Users/SignIn/Index', { flash: {},
    errors: {} })
}
