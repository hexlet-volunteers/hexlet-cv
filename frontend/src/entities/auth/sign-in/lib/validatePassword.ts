export type SignInPasswordErrorKey = 'password.required'

export const validatePassword = (raw: unknown): SignInPasswordErrorKey | null => {
  const password = String(raw ?? '')
  if (!password) return 'password.required'
  return null
}
