import { usePage } from '@inertiajs/react'
import type { PageProps } from '@inertiajs/core'
import {
  Textarea,
  Button,
  SimpleGrid,
  Stack,
  Group,
  Paper,
  Text,
  Title,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { useTranslation } from 'react-i18next'
import type { TCoverLetterData, TCoverProps } from '../types'

export const CoverLetter = ({ onPreview, previewData }: TCoverProps) => {
  const { props } = usePage<PageProps & { coverLetter: TCoverLetterData }>()
  const { t } = useTranslation()

  const tepmplateCover = props.coverLetter

  const form = useForm({
    initialValues: {
      header: '',
      textLetter: '',
    },
  })

  const applyTemplate = () => {
    form.setValues({
      header: tepmplateCover.header ?? '',
      textLetter: tepmplateCover.textLetter ?? '',
    })
  }

  return (
    <SimpleGrid cols={2} spacing="lg">
      <Paper withBorder radius="md" p="lg">
        <form onSubmit={form.onSubmit((values) => onPreview(values))}>
          <Stack>
            <Textarea
              label={t('accountPage.coverLetter.header')}
              autosize
              radius="lg"
              {...form.getInputProps('header')}
            />

            <Textarea
              label={t('accountPage.coverLetter.textLetter')}
              autosize
              minRows={2}
              maxRows={8}
              {...form.getInputProps('textLetter')}
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
                onClick={applyTemplate}
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

      <Paper withBorder radius="md" p="lg">
        <Stack gap="md">
          <Title order={3}>{t('accountPage.coverLetter.preview')}</Title>
          <Text fw="bold">{previewData?.header}</Text>

          <Text>{previewData?.textLetter}</Text>
        </Stack>
      </Paper>
    </SimpleGrid>
  )
}
