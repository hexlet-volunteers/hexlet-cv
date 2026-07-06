import {
  Stack,
  Group,
  Title,
  Text,
  Button,
  SimpleGrid,
  Card,
  Progress,
  Badge,
  Box,
  RingProgress,
} from '@mantine/core'
import {
  overviewStats,
  todayTasks,
  funnel,
  vacancies,
  user,
} from '@shared/mocks/data'

export function Overview() {
  return (
    <Stack gap="lg">
      <Group justify="space-between" align="flex-start">
        <Box>
          <Title order={2}>Добрый день, {user.name.split(' ')[0]}</Title>
          <Text c="dimmed" size="sm">
            Четверг, 3 июля · до собеседования с Т-Банком остался 1 день
          </Text>
        </Box>
        <Group>
          <Button variant="default">Тренировка к собесу</Button>
          <Button>+ Добавить вакансию</Button>
        </Group>
      </Group>

      <SimpleGrid cols={{ base: 1, sm: 2, lg: 4 }}>
        {overviewStats.map((s) => (
          <Card key={s.label} withBorder radius="md" padding="md">
            <Text size="xs" c="dimmed">
              {s.label}
            </Text>
            <Text fw={700} size="xl">
              {s.value}
            </Text>
            {s.progress !== undefined && (
              <Progress value={s.progress} size="sm" mt={6} />
            )}
            <Text size="xs" c="dimmed" mt={4}>
              {s.note}
            </Text>
          </Card>
        ))}
      </SimpleGrid>

      <SimpleGrid cols={{ base: 1, lg: 2 }} spacing="lg">
        <Card withBorder radius="md" padding="lg">
          <Group justify="space-between" mb="sm">
            <Text fw={600}>Сегодня</Text>
            <Badge variant="light">{todayTasks.length} задачи</Badge>
          </Group>
          <Stack gap="sm">
            {todayTasks.map((t) => (
              <Group
                key={t.title}
                justify="space-between"
                wrap="nowrap"
                align="flex-start"
              >
                <Box>
                  <Text size="sm" fw={500}>
                    {t.title}
                  </Text>
                  <Text size="xs" c="dimmed">
                    {t.detail}
                  </Text>
                </Box>
                <Group gap="xs" wrap="nowrap">
                  <Badge size="sm" color="orange" variant="light">
                    {t.badge}
                  </Badge>
                  <Button size="xs" variant="light">
                    {t.action}
                  </Button>
                </Group>
              </Group>
            ))}
          </Stack>
        </Card>

        <Card withBorder radius="md" padding="lg">
          <Text fw={600} mb="sm">
            Воронка поиска
          </Text>
          <Stack gap="xs">
            {funnel.map((f) => (
              <Group key={f.label} justify="space-between">
                <Text size="sm" c="dimmed">
                  {f.label}
                </Text>
                <Group gap="sm" w="70%">
                  <Progress
                    value={(f.count / funnel[0].count) * 100}
                    size="md"
                    style={{ flex: 1 }}
                  />
                  <Text size="sm" fw={600} w={28} ta="right">
                    {f.count}
                  </Text>
                </Group>
              </Group>
            ))}
          </Stack>
          <Text size="xs" c="dimmed" mt="sm">
            Конверсия в ответ 30% — вы в верхней трети выпускников
          </Text>
        </Card>
      </SimpleGrid>

      <Card withBorder radius="md" padding="lg">
        <Group justify="space-between" mb="sm">
          <Text fw={600}>Подходящие вакансии</Text>
          <Text size="sm" c="blue">
            Все 12 новых →
          </Text>
        </Group>
        <Stack gap="sm">
          {vacancies.slice(0, 3).map((v) => (
            <Group key={v.id} justify="space-between" wrap="nowrap">
              <Box>
                <Text size="sm" fw={500}>
                  {v.title}
                </Text>
                <Text size="xs" c="dimmed">
                  {v.company} · {v.salary} · {v.location}
                </Text>
              </Box>
              <Group gap="sm" wrap="nowrap">
                <RingProgress
                  size={44}
                  thickness={4}
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
                    <Text size="10" ta="center" fw={700}>
                      {v.match}
                    </Text>
                  }
                />
                <Button size="xs" variant="light">
                  Открыть
                </Button>
              </Group>
            </Group>
          ))}
        </Stack>
      </Card>
    </Stack>
  )
}
