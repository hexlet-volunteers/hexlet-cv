export type TMenuItem = {
  icon?: React.ElementType
  label: string
  link?: string
}

export type TActivityCardsData = {
  coursesCount: number
  progress: string
  lastResult: {
    courseName: string
    result: string
  }
  nearestEvent: {
    eventName: string
    date: {
      day: string
      time: string
    }
  }
}

export type TPagination = {
  currentPage: number
  totalPages: number
  totalElements: number
  pageSize: number
}

export type User = {
  id: number
  email: string
  roles: string[]
}

export interface AuthProps {
  auth?: {
    user: User
  }
}
