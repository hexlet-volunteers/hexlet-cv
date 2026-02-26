// src/shared/lib/validation/signIn.ts
export const REGEXP_EMIAL = /^[a-z0-9._%+-]+@(?:(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?)\.)+[a-z]{2,}$/i

export const normalizeEmail = (raw: unknown) => String(raw ?? '').trim().toLowerCase()

export type EmailErrorKey = 'email.required' | 'email.invalid_format'

export const validateEmail = (raw: unknown): EmailErrorKey | null => {
  const email = normalizeEmail(raw)
  if (!email) return 'email.required'
  if (!REGEXP_EMIAL.test(email)) return 'email.invalid_format'
  return null
}
