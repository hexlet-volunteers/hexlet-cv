import { Alert, Button, Center, Loader, Stack, Text, Title } from '@mantine/core'
import { Link } from 'react-router'
import type { ApiError } from '@shared/api'

/** Загрузка и ошибки публичных страниц: 404 показываем как обычную страницу «не найдено». */
export function PageState({ loading, error }: { loading: boolean; error: ApiError | null }) {
  if (loading) {
    return (
      <Center py={120}>
        <Loader />
      </Center>
    )
  }
  if (error?.status === 404) {
    return (
      <Center py={120}>
        <Stack align="center" gap="sm">
          <Title order={2}>Страница не найдена</Title>
          <Text c="dimmed">Резюме удалено, скрыто автором или ссылка неверная.</Text>
          <Button component={Link} to="/resumes" variant="light" mt="sm">
            Все резюме
          </Button>
        </Stack>
      </Center>
    )
  }
  if (error) {
    return (
      <Alert color="red" m="xl">
        Не удалось загрузить данные. Попробуйте обновить страницу.
      </Alert>
    )
  }
  return null
}
