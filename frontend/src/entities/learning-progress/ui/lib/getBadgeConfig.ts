import type { ReactNode } from 'react'
import type { TFunction } from 'i18next'
import type { MantineColor } from '@mantine/core'
import type { BadgeStatus } from '@entities/learning-progress/model/getBadgeStatus'

export const getBadgeConfig = (
  status: BadgeStatus,
  t: TFunction,
): { label: ReactNode; color: MantineColor } | null => {
  if (status === 'completed') {
    return {
      label: t('accountPage.progress.programBadge.completedProgram'),
      color: 'green',
    }
  }
  if (status === 'new') {
    return {
      label: t('accountPage.progress.programBadge.newProgram'),
      color: 'blue',
    }
  }
  return null
}
