import type { BadgeStatus } from '../index'
export const getBadgeStatus = (
  isCompleted: boolean,
  completedLessons: number,
): BadgeStatus => {
  if (isCompleted) return 'completed'
  if (completedLessons === 0) return 'new'
  return null
}
