import { Badge, Card, Group, Stack, Text } from '@mantine/core'
import { IconClock } from '@tabler/icons-react'
import type { ApplicationSummary } from '@shared/api'
import { ago, alertTexts, nextStep } from '../lib/format'
import { CompanyAvatar } from './CompanyAvatar'

interface Props {
  application: ApplicationSummary
  onOpen: (id: number) => void
  dragging?: boolean
  onDragStart?: (id: number) => void
}

/** Карточка отклика на доске: компания, роль, версия резюме, подсказки и следующий шаг. */
export function ApplicationCard({
  application: a,
  onOpen,
  dragging,
  onDragStart,
}: Props) {
  const alerts = alertTexts(a)
  const step = nextStep(a)
  const meta =
    a.stage === 'SAVED'
      ? [
          a.salary ?? 'вилка не указана',
          `добавлено ${ago(a.createdAt ?? a.lastEventAt)}`,
        ].join(' · ')
      : a.stage === 'APPLIED'
        ? `отправлен ${ago(a.lastEventAt)}`
        : ''

  return (
    <Card
      withBorder
      radius="md"
      padding="sm"
      draggable
      onDragStart={(e) => {
        e.dataTransfer.setData('text/plain', String(a.id))
        e.dataTransfer.effectAllowed = 'move'
        onDragStart?.(a.id)
      }}
      onClick={() => onOpen(a.id)}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') onOpen(a.id)
      }}
      tabIndex={0}
      role="button"
      aria-label={`${a.position}, ${a.company}`}
      style={{ cursor: 'pointer', opacity: dragging ? 0.4 : 1 }}
    >
      <Group gap="sm" wrap="nowrap" align="flex-start">
        <CompanyAvatar company={a.company} />
        <Stack gap={2} style={{ minWidth: 0 }}>
          <Text fw={700} size="sm" lineClamp={2}>
            {a.position}
          </Text>
          <Text size="xs" c="dimmed">
            {a.company}
          </Text>
        </Stack>
      </Group>
      <Stack gap={6} mt="xs">
        {(a.resume || a.source) && (
          <Group gap={6}>
            {a.resume && (
              <Badge
                tt="none"
                size="sm"
                variant="light"
                color="grape"
                radius="sm"
                maw={200}
              >
                резюме {a.resume.name}
              </Badge>
            )}
            {a.source && a.stage !== 'SAVED' && (
              <Badge
                size="sm"
                variant="light"
                color="gray"
                radius="sm"
                tt="none"
              >
                {a.source}
              </Badge>
            )}
          </Group>
        )}
        {a.stage === 'OFFER' && a.salary && (
          <Text fw={800} c="green.7">
            {a.salary}
          </Text>
        )}
        {step && (
          <Group gap={4} wrap="nowrap">
            <IconClock size={14} color="var(--mantine-color-blue-6)" />
            <Text size="xs" c="blue.7" fw={600}>
              {step}
            </Text>
          </Group>
        )}
        {alerts.map((text) => (
          <Group key={text} gap={4} wrap="nowrap">
            <IconClock size={14} color="var(--mantine-color-red-6)" />
            <Text size="xs" c="red.7" fw={600}>
              {text}
            </Text>
          </Group>
        ))}
        {meta && !step && alerts.length === 0 && (
          <Text size="xs" c="dimmed">
            {meta}
          </Text>
        )}
      </Stack>
    </Card>
  )
}
