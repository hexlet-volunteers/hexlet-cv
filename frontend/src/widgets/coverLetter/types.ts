export type TCoverLetterData = {
  header: string
  textLetter: string
}

export type TCoverProps = {
  onPreview: (value: TCoverLetterData) => void
  previewData: TCoverLetterData | null
}
