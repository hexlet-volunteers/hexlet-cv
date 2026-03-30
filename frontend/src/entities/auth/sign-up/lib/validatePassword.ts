/** Ключ ошибки валидации пароля на форме регистрации. */
export type PasswordErrorKey =
  | 'password.required'
  | 'password.too_short'
  | 'password.too_long'
  | 'password.invalid_format'

/** Минимальная длина пароля на форме регистрации. */
export const PASSWORD_MIN_LEN = 8
/** Максимальная длина пароля на форме регистрации. */
export const PASSWORD_MAX_LEN = 72

const REGEXP_PASSWORD = /^(?=.*[A-Za-z])(?=.*\d)\S+$/

/** Проверяет пароль для формы регистрации и возвращает i18n-ключ ошибки. */
export const validatePassword = (raw: unknown): PasswordErrorKey | null => {
  const password = String(raw ?? '')

  if (!password) return 'password.required'
  if (password.length < PASSWORD_MIN_LEN) return 'password.too_short'
  if (password.length > PASSWORD_MAX_LEN) return 'password.too_long'
  if (!REGEXP_PASSWORD.test(password)) return 'password.invalid_format'

  return null
}
