import {
  Stack,
  Group,
  Title,
  Text,
  Button,
  Card,
  Badge,
  Box,
  ScrollArea,
  SegmentedControl,
} from '@mantine/core'
import { useState } from 'react'
import { trackerColumns } from '@shared/mocks/data'

export function Tracker() {
  const [view, setView] = useState('board')
  return (
    <Stack gap="lg">
      <Group justify="space-between">
        <Box>
          <Title order={2}>Отклики</Title>
          <Text c="dimmed" size="sm">
            9 активных · нажмите на карточку: внутри вся связка — этапы, резюме,
            письма, напоминания
          </Text>
        </Box>
        <Group>
          <SegmentedControl
            value={view}
            onChange={setView}
            data={[
              { label: 'Доска', value: 'board' },
              { label: 'Таблица', value: 'table' },
            ]}
          />
          <Button>+ Добавить отклик</Button>
        </Group>
      </Group>

      <ScrollArea>
        <Group
          align="flex-start"
          wrap="nowrap"
          gap="md"
          style={{ minWidth: 900 }}
        >
          {trackerColumns.map((col) => (
            <Box key={col.id} w={260} style={{ flexShrink: 0 }}>
              <Group gap="xs" mb="sm">
                <Badge color={col.color} variant="dot">
                  {col.title}
                </Badge>
                <Text size="xs" c="dimmed">
                  {col.items.length}
                </Text>
              </Group>
              <Stack gap="sm">
                {col.items.map((a) => (
                  <Card key={a.id} withBorder radius="md" padding="sm">
                    <Text size="sm" fw={600}>
                      {a.title}
                    </Text>
                    <Text size="xs" c="dimmed">
                      {a.company}
                    </Text>
                    <Badge size="xs" variant="light" color="grape" mt={6}>
                      резюме {a.resume}
                    </Badge>
                    <Text
                      size="xs"
                      c={
                        a.note.includes('тишин') || a.note.includes('ответить')
                          ? 'red'
                          : 'dimmed'
                      }
                      mt={6}
                    >
                      {a.note}
                    </Text>
                  </Card>
                ))}
              </Stack>
            </Box>
          ))}
        </Group>
      </ScrollArea>
    </Stack>
  )
}
