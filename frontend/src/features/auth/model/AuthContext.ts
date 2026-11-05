import { createContext } from 'react'
import type { CurrentUser } from '@shared/api'

export interface RegisterInput {
  email: string
  password: string
  firstName: string
  lastName: string
  terms: boolean
}

export interface AuthState {
  /** `undefined` — ещё проверяем сессию, `null` — гость. */
  user: CurrentUser | null | undefined
  login: (email: string, password: string) => Promise<CurrentUser>
  register: (input: RegisterInput) => Promise<CurrentUser>
  logout: () => Promise<void>
}

export const AuthContext = createContext<AuthState | null>(null)
