import type { TCoverLetterData } from '../types'

export const downloadJson = (previewData: TCoverLetterData) => {
  const data = previewData

  const jsonString = JSON.stringify(data, null, 2)
  const blob = new Blob([jsonString], { type: 'application/json' })
  const url = URL.createObjectURL(blob)

  const safeFileName = (data.header || 'resume')
    .replace(/[\\/:*?"<>|]/g, '')
    .trim()

  const link = document.createElement('a')
  link.href = url
  link.download = `${safeFileName}.json`
  link.click()

  URL.revokeObjectURL(url)
}
