import { Group } from '@mantine/core'
import { GoogleButton } from './components/GoogleButton'
import { GithubButton } from './components/GithubButton'
import { useTranslation } from 'react-i18next'

export function SocialButtons() {
  const { t } = useTranslation()

  return (
    <Group grow mb="md" mt="md">
      <GoogleButton href="/auth/oauth/google/start">{t('auth.social.providers.google')}</GoogleButton>
      <GithubButton href="/auth/oauth/github/start">{t('auth.social.providers.github')}</GithubButton>
    </Group>
  )
}
