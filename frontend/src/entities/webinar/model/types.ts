import type { TPagination } from '@shared/types'

export type WebinarDTO = {
  id: number
  title: string
  date: string
  time: string
  isOnline: boolean
  isPublic: boolean
}

export interface WebinarsResponseDTO {
  webinars: WebinarDTO[]
  pagination: TPagination
}
