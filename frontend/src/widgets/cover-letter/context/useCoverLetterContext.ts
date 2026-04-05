import { useContext } from 'react'
import { CoverLetterContext } from './CoverLetterContext'

export const useCoverLetterContext = () => {
  const context = useContext(CoverLetterContext)

  if (!context) {
    throw new Error(
      'useCoverLetterContext must be used within CoverLetterProvider',
    )
  }

  return context
}
