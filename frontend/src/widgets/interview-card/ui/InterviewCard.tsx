import {
  Flex,
  Group,
  Stack,
  Text,
} from '@mantine/core'
import type { FC, ReactNode } from 'react'
import { useTranslation } from 'react-i18next'
import { DsCard } from '@shared/uikit/DsCard/DsCard'
import type { KnowledgeInterviewDTO } from '@entities/interview'

interface InterviewCardProps {
  interview: KnowledgeInterviewDTO
  actionButton: ReactNode
}

export const InterviewCard: FC<InterviewCardProps> = ({
  interview,
  actionButton,
}) => {
  const { t } = useTranslation()
  const typeLabel = t('accountPage.interviews.typeLabel')
  const videoLabel = t('accountPage.interviews.videoLabel')
  const metaLine = interview.duration
    ? `${videoLabel} · ${interview.duration}`
    : videoLabel

  return (
    <DsCard
      shadow="sm"
      padding="md"
      radius="md"
      withBorder
    >
      <DsCard.Content>
        <Flex direction="column" gap="xs">
          <Stack gap={2}>
            <Text fw={700} size="lg" lineClamp={2} lh={1.25}>
              {typeLabel}
              {' · '}
              {interview.title}
            </Text>
            <Text c="dimmed" size="sm">
              {metaLine}
            </Text>
          </Stack>
          <Group justify="flex-start" mt="xs">
            {actionButton}
          </Group>
        </Flex>
      </DsCard.Content>
    </DsCard>
  )
}
