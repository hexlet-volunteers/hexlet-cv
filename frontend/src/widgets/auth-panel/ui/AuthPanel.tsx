import { Group } from '@mantine/core'
import { Login } from './components/Login'
import { AuthorizedEntryPoints } from './components/AuthorizedEntryPoints'

/** Отображает панель авторизационных действий в шапке сайта. */
export function AuthPanel() {
  return (
    <Group gap="sm">
      <AuthorizedEntryPoints />
      <Login />
    </Group>
  )
}
