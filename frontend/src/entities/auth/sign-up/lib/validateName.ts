export type NameErrorKey = 'name.required' | 'name.too_short' | 'name.too_long' | 'name.invalid_format'

export const NAME_MIN_LEN = 2
export const NAME_MAX_LEN = 50

const REGEXP_NAME = /^[A-Za-zА-Яа-яЁё]+(?:[ '\\-][A-Za-zА-Яа-яЁё]+)*$/

export const normalizeName = (raw: unknown) => String(raw ?? '').trim()

export const validateName = (raw: unknown): NameErrorKey | null => {
  const v = normalizeName(raw)

  if (!v) return 'name.required'
  if (v.length < NAME_MIN_LEN) return 'name.too_short'
  if (v.length > NAME_MAX_LEN) return 'name.too_long'
  if (!REGEXP_NAME.test(v)) return 'name.invalid_format'

  return null
}
