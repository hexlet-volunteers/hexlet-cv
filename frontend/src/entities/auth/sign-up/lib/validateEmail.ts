/** Регулярное выражение для базовой проверки email при регистрации. */
export const REGEXP_EMAIL =
  /^[a-z0-9._%+-]+@(?:(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?)\.)+[a-z]{2,}$/i

/** Ключ ошибки валидации email на форме регистрации. */
export type EmailErrorKey =
  | 'email.required'
  | 'email.too_long'
  | 'email.invalid_format'

/** Нормализует email перед валидацией и отправкой формы регистрации. */
export const normalizeEmail = (raw: string) => raw.trim().toLowerCase()

/** Проверяет email для формы регистрации и возвращает i18n-ключ ошибки. */
export const validateEmail = (raw: string): EmailErrorKey | null => {
  const email = normalizeEmail(raw)

  if (!email) return 'email.required'
  if (email.length > 254) return 'email.too_long'
  if (!REGEXP_EMAIL.test(email)) return 'email.invalid_format'

  return null
}
