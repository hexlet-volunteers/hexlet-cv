import type { UserDTO } from '../types'

type SubscriptionStatus = 'none' | 'active' | 'expired'

export function getSubscriptionStatus(user: UserDTO): SubscriptionStatus {
  const now = new Date()

  if (!user.tarif) return 'none'

  const expiryDate = new Date(user.endsAt || '')
  if (isNaN(expiryDate.getTime())) {
    return 'none'
  }

  return expiryDate > now ? 'active' : 'expired'
}
