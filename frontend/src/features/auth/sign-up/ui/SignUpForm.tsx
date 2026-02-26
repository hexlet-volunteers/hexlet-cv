import {
  Anchor,
  Button,
  Checkbox,
  Divider,
  Group,
  PasswordInput,
  Stack,
  Text,
  TextInput,
  Title
} from '@mantine/core'
import classes from './SignUpForm.module.css'
import { Link } from '@inertiajs/react'
import { useSignUp } from '@entities/auth'
import { useTranslation } from 'react-i18next'
import type { TFunction } from 'i18next'

type Props = {
  socialAuth: React.ReactNode
}

const signUpErrorKeyMap: Record<string, string> = {
  'email.required': 'auth.signUp.fields.email.errors.required',
  'email.too_long': 'auth.signUp.fields.email.errors.too_long',
  'email.invalid_format': 'auth.signUp.fields.email.errors.invalid_format',
  'name.required': 'auth.signUp.fields.name.errors.required',
  'name.too_short': 'auth.signUp.fields.name.errors.too_short',
  'name.too_long': 'auth.signUp.fields.name.errors.too_long',
  'name.invalid_format': 'auth.signUp.fields.name.errors.invalid_format',
  'password.required': 'auth.signUp.fields.password.errors.required',
  'password.too_long': 'auth.signUp.fields.password.errors.too_long',
  'password.too_short': 'auth.signUp.fields.password.errors.too_short',
  'password.invalid_format': 'auth.signUp.fields.password.errors.invalid_format',
  'terms.required': 'auth.signUp.fields.terms.errors.required',
}

const getSignUpError = (t: TFunction, error?: string) => {
  if (!error) return undefined

  return t(signUpErrorKeyMap[error] ?? error, { defaultValue: error })
}

export function SignUpForm(props: Props) {
  const { socialAuth } = props

  const { t } = useTranslation()

  const { form, submit } = useSignUp()

  return (
    <form onSubmit={(e) => {
      e.preventDefault()
      submit()
    }}
    >
      <Title ta="center" className={classes.title}>
        {t('auth.signUp.title')}
      </Title>
      <Text className={classes.subtitle}>
        {t('auth.signUp.subtitle')}
      </Text>
      {socialAuth}
      <Divider
        label={t('auth.signUp.divider')}
        labelPosition="center"
        my="lg"
        styles={{ label: { color: 'var(--mantine-color-bright)',
          opacity: 0.85 } }}
      />
      <Stack>
        <TextInput
          error={getSignUpError(t, form.errors.lastName)}
          label={t('auth.signUp.fields.lastName.label')}
          placeholder={t('auth.signUp.fields.lastName.placeholder')}
          value={form.data.lastName}
          onChange={(event) => {
            form.setData('lastName', event.currentTarget.value)
            form.clearErrors('lastName')
          }}
          radius="md"
          withAsterisk
        />
        <TextInput
          error={getSignUpError(t, form.errors.firstName)}
          label={t('auth.signUp.fields.firstName.label')}
          placeholder={t('auth.signUp.fields.firstName.placeholder')}
          value={form.data.firstName}
          onChange={(event) => {
            form.setData('firstName', event.currentTarget.value)
            form.clearErrors('firstName')
          }}
          radius="md"
          withAsterisk
        />
        <TextInput
          label={t('auth.signUp.fields.email.label')}
          placeholder={t('auth.signUp.fields.email.placeholder')}
          value={form.data.email}
          onChange={(event) => {
            form.setData('email', event.currentTarget.value)
            form.clearErrors('email')
          }}
          error={getSignUpError(t, form.errors.email)}
          radius="md"
          withAsterisk
        />
        <PasswordInput
          label={t('auth.signUp.fields.password.label')}
          placeholder={t('auth.signUp.fields.password.placeholder')}
          value={form.data.password}
          onChange={(event) => {
            form.setData('password', event.currentTarget.value)
            form.clearErrors('password')
          }}
          error={getSignUpError(t, form.errors.password)}
          radius="md"
          withAsterisk
        />
        <Checkbox
          label={t('auth.signUp.fields.terms.label')}
          checked={form.data.terms}
          onChange={(event) => {
            form.setData('terms', event.currentTarget.checked)
            form.clearErrors('terms')
          }}
        />
      </Stack>
      <Group justify="space-between" mt="xl">
        <Anchor
          component={Link}
          type="button"
          c="bright"
          opacity={0.85}
          href="/users/sign_in"
          size="xs"
        >
          {t('auth.signUp.signInLink')}
        </Anchor>
        <Button disabled={!form.data.terms} type="submit" radius="xl">
          {t('auth.signUp.submit')}
        </Button>
      </Group>
    </form>
  )
}
