export type TResumeData = {
  header: string
  summary: string
  skills: string
  experience: string
}

export type ResumeProps = {
  onPreview: (value: TResumeData) => void
  previewData: TResumeData | null
}
