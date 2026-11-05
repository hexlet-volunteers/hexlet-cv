import { useState } from 'react'
import { Box, Group, ScrollArea, Stack, Text } from '@mantine/core'
import type { ApplicationStage, ApplicationSummary } from '@shared/api'
import { BOARD_STAGES, STAGE_COLOR, STAGE_LABEL } from '../lib/stages'
import { ApplicationCard } from './ApplicationCard'

interface Props {
  items: ApplicationSummary[]
  onOpen: (id: number) => void
  /** Перенос карточки в другую колонку. */
  onMove: (id: number, stage: ApplicationStage) => void
}

/** Kanban по пяти стадиям воронки; карточки переносятся перетаскиванием (#1156). */
export function Board({ items, onOpen, onMove }: Props) {
  const [over, setOver] = useState<ApplicationStage | null>(null)
  const [dragId, setDragId] = useState<number | null>(null)

  return (
    <ScrollArea type="auto" offsetScrollbars>
      <Group align="flex-start" wrap="nowrap" gap="md" pb="sm">
        {BOARD_STAGES.map((stage) => {
          const column = items.filter((a) => a.stage === stage)
          return (
            <Box
              key={stage}
              w={280}
              p="sm"
              style={{
                flexShrink: 0,
                borderRadius: 12,
                minHeight: 160,
                background:
                  over === stage
                    ? 'var(--mantine-color-blue-0)'
                    : 'var(--mantine-color-gray-1)',
                outline:
                  over === stage
                    ? '2px dashed var(--mantine-color-blue-4)'
                    : 'none',
                transition: 'background 120ms',
              }}
              onDragOver={(e) => {
                e.preventDefault()
                e.dataTransfer.dropEffect = 'move'
                if (over !== stage) setOver(stage)
              }}
              onDragLeave={(e) => {
                if (!e.currentTarget.contains(e.relatedTarget as Node))
                  setOver(null)
              }}
              onDrop={(e) => {
                e.preventDefault()
                setOver(null)
                const id = Number(e.dataTransfer.getData('text/plain'))
                const card = items.find((a) => a.id === id)
                if (card && card.stage !== stage) onMove(id, stage)
              }}
              onDragEnd={() => {
                setDragId(null)
                setOver(null)
              }}
            >
              <Group justify="space-between" mb="sm" px={4}>
                <Group gap={8}>
                  <Box
                    w={8}
                    h={8}
                    style={{
                      borderRadius: '50%',
                      background: `var(--mantine-color-${STAGE_COLOR[stage]}-6)`,
                    }}
                  />
                  <Text fw={700} size="sm">
                    {STAGE_LABEL[stage]}
                  </Text>
                </Group>
                <Text size="sm" c="dimmed">
                  {column.length}
                </Text>
              </Group>
              <Stack gap="sm">
                {column.map((a) => (
                  <ApplicationCard
                    key={a.id}
                    application={a}
                    onOpen={onOpen}
                    dragging={dragId === a.id}
                    onDragStart={setDragId}
                  />
                ))}
                {column.length === 0 && (
                  <Text size="xs" c="dimmed" ta="center" py="md">
                    Перетащите карточку сюда
                  </Text>
                )}
              </Stack>
            </Box>
          )
        })}
      </Group>
    </ScrollArea>
  )
}
