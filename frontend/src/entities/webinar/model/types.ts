import type { Pagination } from '@shared/types/pagination'

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
  pagination: Pagination
}
