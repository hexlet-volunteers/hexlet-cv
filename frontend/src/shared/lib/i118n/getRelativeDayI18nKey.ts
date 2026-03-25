export type RelativeDayStatus = 'today' | 'yesterday' | 'daysAgo'

export const getRelativeDayI18nKey = (days: number): RelativeDayStatus => {
  if (days === 0) return 'today'
  if (days === 1) return 'yesterday'
  return 'daysAgo'
}
