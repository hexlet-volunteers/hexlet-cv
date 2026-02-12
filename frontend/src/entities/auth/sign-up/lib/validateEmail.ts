export const REGEXP_EMAIL = /^[a-z0-9._%+-]+@(?:(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?)\.)+[a-z]{2,}$/i

export type EmailErrorKey = 'email.required' | 'email.too_long' | 'email.invalid_format'

export const normalizeEmail = (raw: string) => raw.trim().toLowerCase()

export const validateEmail = (raw: string): EmailErrorKey | null => {
  const email = normalizeEmail(raw)

  if (!email) return 'email.required'
  if (email.length > 254) return 'email.too_long'
  if (!REGEXP_EMAIL.test(email)) return 'email.invalid_format'

  return null
}
