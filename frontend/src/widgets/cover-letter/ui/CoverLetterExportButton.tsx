import { Button } from '@mantine/core'
import { useCoverLetterContext } from '../context/useCoverLetterContext'
import { useTranslation } from 'react-i18next'
import { downloadJson } from '@shared/lib/helpers/downloadJson'

export const CoverLetterExportButton = () => {
  const { coverLetterData } = useCoverLetterContext()
  const { t } = useTranslation()
  const isDisabled =
    !coverLetterData?.header.trim() && !coverLetterData?.textLetter.trim()

  return (
    <Button
      variant="filled"
      color="yellow"
      radius="md"
      size="md"
      type="button"
      disabled={isDisabled}
      onClick={() =>
        !isDisabled && downloadJson(coverLetterData, 'Cover letter')
      }
    >
      {t('accountPage.coverLetter.download')}
    </Button>
  )
}
