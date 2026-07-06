import {
  Card,
  Stack,
  Title,
  Text,
  Badge,
  Anchor,
  Group,
  ThemeIcon,
} from '@mantine/core'

interface ScreenStubProps {
  title: string
  description: string
  issue?: string
}

// Каркас экрана для команды: вёрстка и данные доводятся по задаче из трекера.
export function ScreenStub({ title, description, issue }: ScreenStubProps) {
  return (
    <Stack gap="lg" maw={720}>
      <Group justify="space-between" align="flex-start">
        <Title order={2}>{title}</Title>
        <Badge color="yellow" variant="light">
          каркас · в разработке
        </Badge>
      </Group>
      <Card withBorder radius="md" padding="xl">
        <Stack gap="sm">
          <Group gap="xs">
            <ThemeIcon variant="light" radius="xl">
              <span>🚧</span>
            </ThemeIcon>
            <Text fw={600}>Экран запланирован в дорожной карте</Text>
          </Group>
          <Text c="dimmed">{description}</Text>
          {issue && (
            <Text size="sm">
              Задача:{' '}
              <Anchor
                href={`https://github.com/hexlet-volunteers/hexlet-cv/issues/${issue.replace('#', '')}`}
                target="_blank"
              >
                {issue}
              </Anchor>
            </Text>
          )}
          <Text size="sm" c="dimmed">
            MVP: экран подключён к навигации и готов к наполнению. Дизайн — в
            docs/design/.
          </Text>
        </Stack>
      </Card>
    </Stack>
  )
}
