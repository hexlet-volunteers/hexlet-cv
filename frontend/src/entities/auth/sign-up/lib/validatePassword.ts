export type PasswordErrorKey = 'password.required' | 'password.too_short' | 'password.too_long' | 'password.invalid_format'

export const PASSWORD_MIN_LEN = 8
export const PASSWORD_MAX_LEN = 72

const REGEXP_PASSWORD = /^(?=.*[A-Za-z])(?=.*\d)\S+$/

export const validatePassword = (raw: unknown): PasswordErrorKey | null => {
  const password = String(raw ?? '')

  if (!password) return 'password.required'
  if (password.length < PASSWORD_MIN_LEN) return 'password.too_short'
  if (password.length > PASSWORD_MAX_LEN) return 'password.too_long'
  if (!REGEXP_PASSWORD.test(password)) return 'password.invalid_format'

  return null
}
