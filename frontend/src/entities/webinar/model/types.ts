import type { TPagination } from '@shared/types'

export type TWebinar = {
  id: number
  title: string
  date: string
  time: string
  isOnline: boolean
  isPublic: boolean
}

export interface IWebinarsResponse {
  webinars: TWebinar[]
  pagination: TPagination
}
