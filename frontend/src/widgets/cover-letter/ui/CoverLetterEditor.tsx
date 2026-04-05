import { Textarea, Button, Stack, Group, Paper } from '@mantine/core'
import { useForm } from '@inertiajs/react'
import { useTranslation } from 'react-i18next'
import { useCoverLetterContext } from '../context/useCoverLetterContext'
import { type TCoverLetterData } from '../types'

export const CoverLetterEditor = () => {
  const { coverLetterData, setCoverLetterData } = useCoverLetterContext()
  const { t } = useTranslation()

  const form = useForm<TCoverLetterData>({
    header: coverLetterData?.header ?? '',
    textLetter: coverLetterData?.textLetter ?? '',
  })

  const handleApplyCoverLetterData = () => {
    form.setData({
      header: coverLetterData?.header ?? '',
      textLetter: coverLetterData?.textLetter ?? '',
    })
  }

  const handleSubmit = () => {
    setCoverLetterData(form.data)
  }

  return (
    <Paper withBorder radius="md" p="lg">
      <form onSubmit={handleSubmit}>
        <Stack>
          <Textarea
            label={t('accountPage.coverLetter.header')}
            autosize
            radius="lg"
            value={form.data.header}
            onChange={(event) =>
              form.setData('header', event.currentTarget.value)
            }
          />

          <Textarea
            label={t('accountPage.coverLetter.textLetter')}
            autosize
            minRows={2}
            maxRows={8}
            value={form.data.textLetter}
            onChange={(event) =>
              form.setData('textLetter', event.currentTarget.value)
            }
            radius="lg"
            styles={{
              input: {
                paddingRight: '60px',
              },
            }}
          />
          <Group>
            <Button
              variant="default"
              radius="md"
              size="md"
              type="button"
              onClick={handleApplyCoverLetterData}
            >
              {t('accountPage.coverLetter.template')}
            </Button>
            <Button
              variant="filled"
              color="yellow"
              radius="md"
              size="md"
              type="submit"
            >
              {t('accountPage.coverLetter.preview')}
            </Button>
          </Group>
        </Stack>
      </form>
    </Paper>
  )
}
