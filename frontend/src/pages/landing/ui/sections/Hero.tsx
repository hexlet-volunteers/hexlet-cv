import { Container, Stack, Title, Text, Group, Button } from '@mantine/core'
import { Link } from 'react-router'

export function Hero() {
  return (
    <Container size="lg" py={64}>
      <Stack align="center" gap="lg" ta="center">
        <Title order={1} size={48} maw={800}>
          От отклика до оффера — в одном сервисе
        </Title>
        <Text size="xl" c="dimmed" maw={640}>
          Вакансии с трёх площадок, резюме с AI-ревью, письма, трекер откликов и
          тренировочные собеседования
        </Text>
        <Group>
          <Button size="lg" component={Link} to="/register">
            Начать бесплатно
          </Button>
          <Button size="lg" variant="default" component={Link} to="/app">
            Смотреть демо
          </Button>
        </Group>
        <Text size="sm" c="dimmed">
          Бесплатно для студентов Хекслета · без карты
        </Text>
      </Stack>
    </Container>
  )
}
