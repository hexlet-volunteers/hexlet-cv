type TokenRec = { userId: string, exp: number }

const access = new Map<string, TokenRec>()
const refresh = new Map<string, TokenRec>()

const now = () => Date.now()
const rnd = () => crypto.randomUUID()

const ACCESS_TTL_MS = 10 * 60_000
const REFRESH_TTL_MS = 14 * 24 * 60 * 60_000

export function issueTokens(userId: string) {
  const accessToken = `a.${rnd()}`
  const refreshToken = `r.${rnd()}`

  access.set(accessToken, { userId,
    exp: now() + ACCESS_TTL_MS })
  refresh.set(refreshToken, { userId,
    exp: now() + REFRESH_TTL_MS })

  return { accessToken,
    refreshToken }
}

export function validateAccess(token?: string) {
  if (!token) return null
  const rec = access.get(token)
  if (!rec) return null
  if (rec.exp <= now()) return null
  return rec
}

export function validateRefresh(token?: string) {
  if (!token) return null
  const rec = refresh.get(token)
  if (!rec) return null
  if (rec.exp <= now()) return null
  return rec
}

export function rotateAccess(refreshToken?: string) {
  const r = validateRefresh(refreshToken)
  if (!r) return null

  const newAccess = `a.${rnd()}`
  access.set(newAccess, { userId: r.userId,
    exp: now() + ACCESS_TTL_MS })

  return { userId: r.userId,
    accessToken: newAccess }
}

export function revoke(accessToken?: string, refreshToken?: string) {
  if (accessToken) access.delete(accessToken)
  if (refreshToken) refresh.delete(refreshToken)
}
