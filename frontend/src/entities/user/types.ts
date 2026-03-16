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

export function getSubscriptionStatus(user: UserDTO): 'none' | 'active' | 'expired' {
  const now = new Date()

  if (!user.tarif) return 'none'

  const expiryDate = new Date(user.endsAt || '')
  if (isNaN(expiryDate.getTime())) {
    return 'none'
  }

  return expiryDate > now ? 'active' : 'expired'
}
