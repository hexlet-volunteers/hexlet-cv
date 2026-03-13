import type { TPagination } from '@shared/types/inertiaSharedData/inertiaSharedProps'

export type KnowledgeInterviewDTO = {
  id: number
  title: string
  description: string | null
  duration: string | null
}

export type KnowledgeInterviewsResponseDTO = {
  interviews: KnowledgeInterviewDTO[]
  pagination: TPagination
}

export type KnowledgeInterviewShowResponseDTO = {
  interview: KnowledgeInterviewDTO
}
