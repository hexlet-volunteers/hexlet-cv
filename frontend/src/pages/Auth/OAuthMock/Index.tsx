import { Button, Group, Stack, Text, Title } from '@mantine/core'
import { Link, router, usePage } from '@inertiajs/react'
import type { PageProps } from '@inertiajs/core'

/**
 * Описывает пропсы мок-страницы OAuth-провайдера.
 */
interface OAuthMockProps extends PageProps {
  /** URL, на который нужно вернуться после завершения авторизации. */
  next: string
  /** Идентификатор OAuth-провайдера. */
  provider: string
  /** Значение state для валидации OAuth-потока. */
  state: string
}

/**
 * TODO: Перенести в Users, и поменять рест path на users.
 */
/**
 * Эмулирует внешний OAuth-провайдер для локальной разработки и моков.
 */
export default function OAuthMock() {
  const { props } = usePage<OAuthMockProps>()
  const { provider, state, next, locale } = props

  const approve = () => {
    router.get(
      `/${locale}/auth/oauth/${provider}/callback`,
      { state, code: 'mock', next },
      { preserveScroll: true },
    )
  }

  return (
    <Stack maw={420} mx="auto" mt="xl">
      <Title order={2}>Вход через {provider}</Title>
      <Text opacity={0.8}>
        Это мок OAuth-провайдера. Нажмите “Подтвердить”, чтобы эмулировать
        успешную авторизацию.
      </Text>

      <Group justify="space-between" mt="md">
        <Button variant="default" component={Link} href="/users/sign_in">
          Отмена
        </Button>
        <Button onClick={approve}>Подтвердить</Button>
      </Group>
    </Stack>
  )
}
