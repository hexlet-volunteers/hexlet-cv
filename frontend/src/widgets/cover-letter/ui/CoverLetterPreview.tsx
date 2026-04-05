import { Stack, Paper, Text, Title } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { useCoverLetterContext } from '../context/useCoverLetterContext'

export const CoverLetterPreview = () => {
  const { coverLetterData } = useCoverLetterContext()
  const { t } = useTranslation()

  return (
    <Paper withBorder radius="md" p="lg">
      <Stack gap="md">
        <Title order={3}>{t('accountPage.coverLetter.preview')}</Title>
        <Text fw="bold">{coverLetterData?.header}</Text>

        <Text>{coverLetterData?.textLetter}</Text>
      </Stack>
    </Paper>
  )
}
