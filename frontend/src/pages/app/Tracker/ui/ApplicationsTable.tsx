import { useState } from 'react'
import { Badge, Group, Stack, Table, Text, UnstyledButton } from '@mantine/core'
import {
  IconChevronDown,
  IconChevronUp,
  IconSelector,
} from '@tabler/icons-react'
import type { ApplicationSummary } from '@shared/api'
import { ago, alertTexts, nextStep, parse } from '../lib/format'
import { STAGE_COLOR, STAGE_ORDER, STAGE_SHORT } from '../lib/stages'
import { CompanyAvatar } from './CompanyAvatar'

type SortKey = 'updated' | 'stage'

interface Props {
  items: ApplicationSummary[]
  onOpen: (id: number) => void
}

function SortHeader({
  label,
  active,
  desc,
  onClick,
}: {
  label: string
  active: boolean
  desc: boolean
  onClick: () => void
}) {
  const Icon = !active ? IconSelector : desc ? IconChevronDown : IconChevronUp
  return (
    <UnstyledButton onClick={onClick}>
      <Group gap={4} wrap="nowrap">
        <Text size="xs" fw={700} c="dimmed" tt="uppercase">
          {label}
        </Text>
        <Icon size={14} />
      </Group>
    </UnstyledButton>
  )
}

/** Табличный вид трекера с сортировкой по дате и этапу (#1157). */
export function ApplicationsTable({ items, onOpen }: Props) {
  const [sort, setSort] = useState<{ key: SortKey; desc: boolean }>({
    key: 'updated',
    desc: true,
  })

  const sorted = [...items].sort((x, y) => {
    const diff =
      sort.key === 'updated'
        ? parse(x.lastEventAt).getTime() - parse(y.lastEventAt).getTime()
        : STAGE_ORDER[y.stage] - STAGE_ORDER[x.stage]
    return sort.desc ? -diff : diff
  })

  const toggle = (key: SortKey) =>
    setSort((s) =>
      s.key === key ? { key, desc: !s.desc } : { key, desc: true },
    )

  const head = (text: string) => (
    <Text size="xs" fw={700} c="dimmed" tt="uppercase">
      {text}
    </Text>
  )

  return (
    <Table.ScrollContainer minWidth={820}>
      <Table highlightOnHover verticalSpacing="sm">
        <Table.Thead>
          <Table.Tr>
            <Table.Th>{head('Вакансия')}</Table.Th>
            <Table.Th>
              <SortHeader
                label="Этап"
                active={sort.key === 'stage'}
                desc={sort.desc}
                onClick={() => toggle('stage')}
              />
            </Table.Th>
            <Table.Th>{head('Резюме')}</Table.Th>
            <Table.Th>{head('Источник')}</Table.Th>
            <Table.Th>
              <SortHeader
                label="Обновлено"
                active={sort.key === 'updated'}
                desc={sort.desc}
                onClick={() => toggle('updated')}
              />
            </Table.Th>
            <Table.Th>{head('Следующий шаг')}</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {sorted.map((a) => {
            const alerts = alertTexts(a)
            const step = nextStep(a)
            return (
              <Table.Tr
                key={a.id}
                onClick={() => onOpen(a.id)}
                style={{ cursor: 'pointer' }}
              >
                <Table.Td>
                  <Group gap="sm" wrap="nowrap">
                    <CompanyAvatar company={a.company} size={30} />
                    <Stack gap={0}>
                      <Text fw={700} size="sm">
                        {a.position}
                      </Text>
                      <Text size="xs" c="dimmed">
                        {a.company}
                      </Text>
                    </Stack>
                  </Group>
                </Table.Td>
                <Table.Td>
                  <Badge
                    tt="none"
                    variant="light"
                    color={STAGE_COLOR[a.stage]}
                    radius="sm"
                  >
                    {STAGE_SHORT[a.stage]}
                  </Badge>
                </Table.Td>
                <Table.Td>
                  <Text size="sm">{a.resume?.name ?? '—'}</Text>
                </Table.Td>
                <Table.Td>
                  <Text size="sm">{a.source ?? '—'}</Text>
                </Table.Td>
                <Table.Td>
                  <Text size="sm" c="dimmed">
                    {ago(a.lastEventAt)}
                  </Text>
                </Table.Td>
                <Table.Td>
                  {alerts.length > 0 ? (
                    <Text size="sm" fw={600} c="red.7">
                      {alerts.join(' · ')}
                    </Text>
                  ) : step ? (
                    <Text size="sm" fw={600} c="blue.7">
                      {step}
                    </Text>
                  ) : (
                    <Text size="sm" c="dimmed">
                      —
                    </Text>
                  )}
                </Table.Td>
              </Table.Tr>
            )
          })}
        </Table.Tbody>
      </Table>
    </Table.ScrollContainer>
  )
}
