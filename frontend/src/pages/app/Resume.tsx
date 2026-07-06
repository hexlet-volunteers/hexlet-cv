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
  Tabs,
} from '@mantine/core'
import { resumeVersions, resumeSkills } from '@shared/mocks/data'

export function Resume() {
  const main = resumeVersions[0]
  return (
    <Stack gap="lg">
      <Group justify="space-between">
        <Title order={2}>Резюме</Title>
        <Group>
          <Button variant="default">Экспорт</Button>
          <Button>Адаптировать под вакансию</Button>
        </Group>
      </Group>

      <Tabs defaultValue="ru">
        <Tabs.List>
          <Tabs.Tab value="ru">РУ</Tabs.Tab>
          <Tabs.Tab value="en" disabled>
            EN · скоро
          </Tabs.Tab>
        </Tabs.List>
      </Tabs>

      <Card withBorder radius="md" padding="lg">
        <Group justify="space-between">
          <Box>
            <Group gap="xs">
              <Text fw={600}>Версия {main.version}</Text>
              <Badge variant="light">{main.label}</Badge>
            </Group>
            <Text size="sm" c="dimmed" mt={4}>
              {main.hint}
            </Text>
            <Button size="xs" variant="light" mt="sm">
              Вставить все подтверждённые — из моих упражнений
            </Button>
          </Box>
          <RingProgress
            size={90}
            thickness={8}
            roundCaps
            sections={[
              { value: main.score, color: main.score >= 85 ? 'green' : 'blue' },
            ]}
            label={
              <Text ta="center" fw={700}>
                {main.score}/100
              </Text>
            }
          />
        </Group>
      </Card>

      <Card withBorder radius="md" padding="lg">
        <Text fw={600} mb="sm">
          Навыки — подтверждены практикой
        </Text>
        <Group gap={6}>
          {resumeSkills.map((s) => (
            <Badge key={s} variant="light">
              {s}
            </Badge>
          ))}
        </Group>
        <Text size="sm" c="dimmed" mt="sm">
          За навыками — 214 упражнений и 4 проекта с код-ревью в Хекслете
        </Text>
      </Card>
    </Stack>
  )
}
