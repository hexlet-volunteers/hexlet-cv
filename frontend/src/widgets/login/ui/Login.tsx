import { Link, useForm, usePage } from '@inertiajs/react'
import { Button, Group, Text } from '@mantine/core'
import { useTranslation } from 'react-i18next'

export function Login() {
  const { t } = useTranslation()
  const { props: pageProps } = usePage()
  const form = useForm()
  const { auth } = pageProps

  const isNewUser = JSON.parse(localStorage.getItem('IS_NEW_USER') || 'false')

  if (auth?.user) return (
    <Button
      onClick={() => form.post('/users/sign_out')}
      size="md"
      variant="default"
    >
      <Text size="md" lh={1.1}>
        {t('header.auth.signOut')}
      </Text>
    </Button>
  )

  if (!isNewUser) return (
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
        <Text size="md" lh={1.1}>
          {t('header.auth.signIn')}
        </Text>
      </Button>
      <Button
        component={Link}
        href="/users/sign_up"
        variant="default"
        size="md"
      >
        <Text size="md" lh={1.1}>
          {t('header.auth.signUp')}
        </Text>
      </Button>
    </Group>
  )
}
