import { type ReactNode } from 'react'
export type TCoverLetterData = {
  header: string
  textLetter: string
}

export type TCoverLetterContextValue = {
  coverLetterData?: TCoverLetterData
  setCoverLetterData: (data: TCoverLetterData) => void
}
export type TCoverLetterProviderProps = {
  children: ReactNode
  initialData: TCoverLetterData
}
