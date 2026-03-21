export enum EUserRole {
  ADMIN = 'ADMIN',
  USER = 'USER',
  CURATOR = 'CURATOR',
  СONSULTANT = 'СONSULTANT',
  MENTOR = 'MENTOR'
}

export type UserDTO = {
  id: number
  name: string
  email: string
  login: string
  role: keyof typeof EUserRole
  isSubscribed: boolean | null
  tarif: 'pro' | null
  startsAt?: string | null
  endsAt?: string | null
}
