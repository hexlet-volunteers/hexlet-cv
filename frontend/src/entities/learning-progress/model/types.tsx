import type { TPagination } from '@shared/types'

export type BadgeStatus = 'completed' | 'new' | null

export type LearningProgressDTO = {
  id: number
  programTitle: string
  lastLessonTitle: string
  completedLessons: number
  totalLessons: number
  progressPercentage: number
  isCompleted: boolean
  startedAt: string
  lastActivityAt: string
}

export interface IProgressResponse {
  progress: LearningProgressDTO[]
  pagination: TPagination
}
