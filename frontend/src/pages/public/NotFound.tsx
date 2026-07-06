import { Link } from 'react-router'
import { Container, Title, Button, Stack } from '@mantine/core'

export function NotFound() {
  return (
    <Container size="sm" py={100}>
      <Stack align="center" ta="center" gap="md">
        <Title order={1} size={72}>404</Title>
        <Title order={2}>Такой страницы нет. Зато есть вакансии</Title>
        <Button component={Link} to="/app" size="lg">Открыть приложение</Button>
      </Stack>
    </Container>
  )
}
