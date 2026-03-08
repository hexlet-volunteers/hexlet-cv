export const ROLES = {
  ADMIN: 'Администратор',
  USER: 'Пользователь',
  CURATOR: 'Куратор',
  СONSULTANT: 'Карьерный консультант',
  MENTOR: 'Наставник',
} as const

export type UserRole = typeof ROLES[keyof typeof ROLES]
