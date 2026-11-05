import { Center, Loader } from '@mantine/core'
import type { ReactNode } from 'react'
import { Navigate, useLocation } from 'react-router'
import { useAuth } from '../model/useAuth'

/** Пускает только вошедших: гостя отправляет на `/login` и возвращает обратно после входа. */
export function RequireAuth({ children }: { children: ReactNode }) {
  const { user } = useAuth()
  const location = useLocation()

  if (user === undefined) {
    return (
      <Center py={80}>
        <Loader />
      </Center>
    )
  }
  if (user === null) {
    const back = encodeURIComponent(location.pathname + location.search)
    return <Navigate to={`/login?next=${back}`} replace />
  }
  return children
}
