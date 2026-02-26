import {
  Button,
  Divider,
  Group,
  PasswordInput,
  Stack,
  Text,
  TextInput,
  Title
} from '@mantine/core'
import classes from './SignInForm.module.css'
import { useSignIn } from '@entities/auth'
import { useTranslation } from 'react-i18next'
import type { TFunction } from 'i18next'

const signInErrorKeyMap: Record<string, string> = {
  'email.required': 'auth.signIn.fields.email.errors.required',
  'email.invalid': 'auth.signIn.fields.email.errors.invalid',
  'email.invalid_format': 'auth.signIn.fields.email.errors.invalid_format',
  'password.required': 'auth.signIn.fields.password.errors.required',
}

const getSignInError = (t: TFunction, error?: string) => {
  if (!error) return undefined

  return t(signInErrorKeyMap[error] ?? error, { defaultValue: error })
}

interface IProps {
  socialAuth: React.ReactNode
}

export function SignInForm(props: IProps) {
  const { socialAuth } = props

  const { t } = useTranslation()
  const { form, submit } = useSignIn()

  return (
    <form onSubmit={(e) => {
      e.preventDefault()
      submit()
    }}
    >
      <Title ta="center" className={classes.title}>
        {t('auth.signIn.title')}
      </Title>
      <Text className={classes.subtitle}>
        {t('auth.signIn.subtitle')}
      </Text>
      {socialAuth}
      <Divider
        label={t('auth.signIn.divider')}
        labelPosition="center"
        my="lg"
        styles={{ label: { color: 'var(--mantine-color-bright)',
          opacity: 0.85 } }}
      />
      <Stack>
        <TextInput
          label={t('auth.signIn.fields.email.label')}
          placeholder={t('auth.signIn.fields.email.placeholder')}
          value={form.data.email}
          onChange={(event) => {
            form.setData('email', event.currentTarget.value)
            form.clearErrors('email')
          }}
          error={getSignInError(t, form.errors.email)}
          radius="md"
          withAsterisk
        />
        <PasswordInput
          label={t('auth.signIn.fields.password.label')}
          placeholder={t('auth.signIn.fields.password.placeholder')}
          value={form.data.password}
          onChange={(event) => {
            form.setData('password', event.currentTarget.value)
            form.clearErrors('password')
          }}
          error={getSignInError(t, form.errors.password)}
          radius="md"
          withAsterisk
        />
      </Stack>
      <Group justify="space-between" mt="xl">
        <Button type="submit" radius="xl">
          {t('auth.signIn.submit')}
        </Button>
      </Group>
    </form>
  )
}
