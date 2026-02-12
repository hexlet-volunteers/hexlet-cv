// src/shared/lib/validation/signIn.ts
/** Регулярное выражение для базовой проверки email при входе. */
export const REGEXP_EMIAL =
  /^[a-z0-9._%+-]+@(?:(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?)\.)+[a-z]{2,}$/i

/** Нормализует email перед валидацией и отправкой формы входа. */
export const normalizeEmail = (raw: unknown) =>
  String(raw ?? '')
    .trim()
    .toLowerCase()

/** Ключ ошибки валидации email на форме входа. */
export type EmailErrorKey = 'email.required' | 'email.invalid_format'

/** Проверяет email для формы входа и возвращает i18n-ключ ошибки. */
export const validateEmail = (raw: unknown): EmailErrorKey | null => {
  const email = normalizeEmail(raw)
  if (!email) return 'email.required'
  if (!REGEXP_EMIAL.test(email)) return 'email.invalid_format'
  return null
}
