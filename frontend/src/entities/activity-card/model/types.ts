export type ActivityCardDTO = {
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
