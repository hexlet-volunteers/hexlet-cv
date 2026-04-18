import { Link, useForm, usePage } from '@inertiajs/react'
import { Button, Group, Text } from '@mantine/core'
import { useTranslation } from 'react-i18next'

const SHOW_FULL_AUTH_ACTIONS_KEY = 'SHOW_FULL_AUTH_ACTIONS'

const getShouldShowFullAuthActions = () => {
  try {
    return localStorage.getItem(SHOW_FULL_AUTH_ACTIONS_KEY) === 'true'
  } catch {
    return false
  }
}

/** Отображает кнопки входа, регистрации или выхода в зависимости от состояния пользователя. */
export function Login() {
  const { t } = useTranslation()
  const { props } = usePage()
  const form = useForm()

  const shouldShowFullAuthActions = getShouldShowFullAuthActions()

  if (props.auth?.user) {
    return (
      <Button
        onClick={() => form.post('/users/sign_out', { preserveScroll: true })}
        disabled={form.processing}
        loading={form.processing}
        size="md"
        variant="default"
      >
        {t('header.auth.signOut')}
      </Button>
    )
  }

  if (!shouldShowFullAuthActions)
    return (
      <Button
        component={Link}
        href="/users/sign_up"
        size="md"
        variant="default"
      >
        <Text size="md" lh={1.1}>
          {t('header.auth.tryFreeLine1')}
          <br />
          {t('header.auth.tryFreeLine2')}
        </Text>
      </Button>
    )

  return (
    <Group>
      <Button
        component={Link}
        href="/users/sign_in"
        variant="default"
        size="md"
      >
        {t('header.auth.signIn')}
      </Button>
      <Button
        component={Link}
        href="/users/sign_up"
        variant="default"
        size="md"
      >
        {t('header.auth.signUp')}
      </Button>
    </Group>
  )
}
