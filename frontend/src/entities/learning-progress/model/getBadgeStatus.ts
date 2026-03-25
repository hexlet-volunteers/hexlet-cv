export type BadgeStatus = 'completed' | 'new' | null

export const getBadgeStatus = (
  isCompleted: boolean,
  completedLessons: number,
): BadgeStatus => {
  if (isCompleted) return 'completed'
  if (completedLessons === 0) return 'new'
  return null
}
