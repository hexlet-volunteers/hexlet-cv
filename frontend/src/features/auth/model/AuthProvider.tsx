import {
  useCallback,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { api, type CurrentUser } from '@shared/api'
import { AuthContext, type RegisterInput } from './AuthContext'

/**
 * Хранит текущего пользователя. При старте спрашивает `/api/auth/me`:
 * сессия живёт в HttpOnly-куках, и другого способа узнать о ней у фронта нет.
 */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<CurrentUser | null | undefined>(undefined)

  useEffect(() => {
    api<CurrentUser>('/api/auth/me')
      .then(setUser)
      .catch(() => setUser(null))
  }, [])

  const login = useCallback(async (email: string, password: string) => {
    const me = await api<CurrentUser>('/api/auth/login', {
      method: 'POST',
      body: { email, password },
    })
    setUser(me)
    return me
  }, [])

  const register = useCallback(async (input: RegisterInput) => {
    const me = await api<CurrentUser>('/api/auth/register', {
      method: 'POST',
      body: input,
    })
    setUser(me)
    return me
  }, [])

  const logout = useCallback(async () => {
    await api<void>('/api/auth/logout', { method: 'POST' }).catch(
      () => undefined,
    )
    setUser(null)
  }, [])

  const value = useMemo(
    () => ({ user, login, register, logout }),
    [user, login, register, logout],
  )
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
