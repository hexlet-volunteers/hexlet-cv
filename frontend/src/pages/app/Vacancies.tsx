import { useState } from 'react'
import {
  Stack,
  Group,
  Title,
  Text,
  Button,
  Card,
  Badge,
  RingProgress,
  Box,
  Chip,
  TextInput,
} from '@mantine/core'
import { vacancies } from '@shared/mocks/data'

const filters = [
  'Все 47',
  'Новые 12',
  'React 18',
  'Удалённо 26',
  'С зарплатой 31',
]

export function Vacancies() {
  const [active, setActive] = useState('Все 47')
  return (
    <Stack gap="lg">
      <Box>
        <Title order={2}>Вакансии</Title>
        <Text c="dimmed" size="sm">
          12 новых под ваш профиль · hh.ru, Хабр Карьера, Getmatch, Telegram,
          фриланс и мировые площадки
        </Text>
      </Box>

      <Card withBorder radius="md" padding="md">
        <Group>
          <TextInput
            style={{ flex: 1 }}
            placeholder="https://hh.ru/vacancy/… или ссылка из Telegram"
          />
          <Button>Разобрать</Button>
        </Group>
        <Text size="xs" c="dimmed" mt="xs">
          AI сам подтянет компанию, роль, вилку и дедлайн
        </Text>
      </Card>

      <Group>
        {filters.map((f) => (
          <Chip
            key={f}
            checked={active === f}
            onChange={() => setActive(f)}
            variant="filled"
          >
            {f}
          </Chip>
        ))}
      </Group>

      <Stack gap="md">
        {vacancies.map((v) => (
          <Card key={v.id} withBorder radius="md" padding="lg">
            <Group justify="space-between" wrap="nowrap" align="flex-start">
              <Box style={{ flex: 1 }}>
                <Group gap="xs">
                  <Text fw={600}>{v.title}</Text>
                  {v.isNew && (
                    <Badge size="sm" color="blue">
                      новая
                    </Badge>
                  )}
                </Group>
                <Text size="sm" c="dimmed">
                  {v.company} · {v.location} · {v.salary}
                </Text>
                <Text size="xs" c="dimmed" mt={2}>
                  {v.source} · {v.posted}
                </Text>
                <Group gap={6} mt="sm">
                  {v.stack.map((s) => (
                    <Badge key={s} variant="light" color="gray">
                      {s}
                    </Badge>
                  ))}
                </Group>
              </Box>
              <Stack align="center" gap="xs">
                <RingProgress
                  size={64}
                  thickness={5}
                  roundCaps
                  sections={[
                    {
                      value: v.match,
                      color:
                        v.match >= 90
                          ? 'green'
                          : v.match >= 85
                            ? 'teal'
                            : 'blue',
                    },
                  ]}
                  label={
                    <Text ta="center" fw={700}>
                      {v.match}%
                    </Text>
                  }
                />
              </Stack>
            </Group>
            <Group mt="md">
              <Button size="sm">Откликнуться</Button>
              <Button size="sm" variant="light">
                Адаптировать резюме
              </Button>
            </Group>
          </Card>
        ))}
      </Stack>
    </Stack>
  )
}
