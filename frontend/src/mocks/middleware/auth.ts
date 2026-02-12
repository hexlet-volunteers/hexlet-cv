import type { Middleware } from '@mocks/msw/pipeline'
import { mockDbUsers } from '@mocks/state/mockDb'
import { rotateAccess, validateAccess } from '@mocks/state/tokenStore'

const isProtected = (pathNoLocale: string) => {
  return (
    /^\/account(?:\/|$)/.test(pathNoLocale)
    || /^\/admin(?:\/|$)/.test(pathNoLocale)
  )
}

export const authMiddleware: Middleware = (ctx) => {
  const accessToken = ctx.cookies.access_token
  const refreshToken = ctx.cookies.refresh_token

  // 1. Валидный access -> пользователь авторизован
  const access = validateAccess(accessToken)
  if (access) {
    const user = mockDbUsers.findById(Number(access.userId))

    if (user) {
      ctx.user = {
        id: user.id,
        email: user.email,
        roles: user.roles,
      }
    }

    return
  }

  // 2. Access протух, но refresh жив -> вращаем access
  const rotated = rotateAccess(refreshToken)
  if (rotated) {
    ctx.setCookie('access_token', rotated.accessToken, { maxAge: 10 * 60 })

    const user = mockDbUsers.findById(Number(rotated.userId))
    if (user) {
      ctx.user = {
        id: user.id,
        email: user.email,
        roles: user.roles,
      }
    }

    return
  }

  // 3. Нет валидных токенов
  if (accessToken) ctx.clearCookie('access_token')
  if (refreshToken) ctx.clearCookie('refresh_token')

  if (!isProtected(ctx.pathNoLocale)) return

  return ctx.inertiaPage(
    'Users/SignIn/Index',
    {
      flash: {},
      errors: {},
      auth: { user: null },
    },
    200,
    `/${ctx.locale}/users/sign_in`
  )
}