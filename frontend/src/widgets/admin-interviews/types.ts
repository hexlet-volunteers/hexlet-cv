export type InterviewsEntry = {
  id: number
  title: string
  speaker: string
  videoUrl: string
  isPublished: boolean
}

export type TProps = {
  interviews: InterviewsEntry[]
}
