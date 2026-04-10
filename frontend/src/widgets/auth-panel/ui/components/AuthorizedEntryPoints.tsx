import { Button, Group } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { Link, usePage } from '@inertiajs/react'

/** Отображает ссылки на доступные пользователю внутренние разделы. */
export function AuthorizedEntryPoints() {
  const { t } = useTranslation()
  const { props } = usePage()

  const user = props.auth?.user
  if (!user) return null

  const isAdmin = user.roles.includes('admin')

  return (
    <Group gap="sm">
      <Button
        component={Link}
        href="/account/my-progress"
        variant="light"
        size="md"
      >
        {t('header.auth.account')}
      </Button>

      {isAdmin && (
        <Button
          component={Link}
          href="/admin/interview"
          variant="light"
          size="md"
        >
          {t('header.auth.admin')}
        </Button>
      )}
    </Group>
  )
}
