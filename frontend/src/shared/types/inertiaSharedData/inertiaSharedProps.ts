export type MenuItem = {
  icon?: React.ElementType
  label: string
  link?: string
}

export type ActivityCardsData = {
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
