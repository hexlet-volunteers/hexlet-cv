type OAuthStateRec = {
  provider: string
  locale: string
  next: string
  exp: number
}

const TTL_MS = 5 * 60_000
const store = new Map<string, OAuthStateRec>()

const now = () => Date.now()

export function issueOAuthState(input: Omit<OAuthStateRec, 'exp'>) {
  const state = crypto.randomUUID()
  store.set(state, { ...input,
    exp: now() + TTL_MS })
  return state
}

export function consumeOAuthState(state: string, provider: string) {
  const rec = store.get(state)
  if (!rec) return null

  store.delete(state)

  if (rec.exp <= now()) return null
  if (rec.provider !== provider) return null

  return rec
}

export function clearOAuthStates() {
  store.clear()
}
