/** Ключ ошибки валидации пароля на форме входа. */
export type SignInPasswordErrorKey = 'password.required'

/** Проверяет наличие пароля на форме входа. */
export const validatePassword = (
  raw: unknown,
): SignInPasswordErrorKey | null => {
  const password = String(raw ?? '')
  if (!password) return 'password.required'
  return null
}
