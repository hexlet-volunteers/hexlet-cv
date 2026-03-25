import type { TPagination } from '@shared/types'

export type TProgress = {
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
  progress: TProgress[]
  pagination: TPagination
}
