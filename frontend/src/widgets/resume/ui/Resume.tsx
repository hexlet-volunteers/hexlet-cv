import { usePage } from '@inertiajs/react'
import type { PageProps } from '@inertiajs/core'
import { Textarea, Button, SimpleGrid, Stack, Group, Paper, Text, Title } from '@mantine/core'
import { useForm } from '@mantine/form'
import { useTranslation } from 'react-i18next'
import type { TResumeData, ResumeProps } from '../types'

export const Resume = ({ onPreview, previewData }: ResumeProps) => {
  const { props } = usePage<PageProps & { resume: TResumeData }>()
  const { t } = useTranslation()

  const templateResume = props.resume

  const form = useForm({
    initialValues: {
      header: '',
      summary: '',
      skills: '',
      experience: '',
    },
  })

  const applyTemplate = () => {
    form.setValues({
      header: templateResume.header ?? '',
      summary: templateResume.summary ?? '',
      skills: templateResume.skills ?? '',
      experience: templateResume.experience ?? '',
    })
  }

  return (

    <SimpleGrid cols={2} spacing="lg">
      <Paper withBorder radius="md" p="lg">
        <form onSubmit={form.onSubmit(values => onPreview(values))}>
          <Stack>
            <Textarea
              label={t('accountPage.resume.header')}
              autosize
              minRows={2}
              maxRows={4}
              {...form.getInputProps('header')}
            />

            <Textarea
              label={t('accountPage.resume.summary')}
              autosize
              minRows={2}
              maxRows={4}
              {...form.getInputProps('summary')}
            />

            <Textarea
              label={t('accountPage.resume.skills')}
              autosize
              minRows={2}
              maxRows={4}
              {...form.getInputProps('skills')}
            />

            <Textarea
              label={t('accountPage.resume.experience')}
              autosize
              minRows={2}
              maxRows={4}
              {...form.getInputProps('experience')}
            />
            <Group>
              <Button
                variant="default"
                radius="md"
                size="md"
                type="button"
                onClick={applyTemplate}
              >
                {t('accountPage.resume.template')}
              </Button>
              <Button
                variant="filled"
                color="yellow"
                radius="md"
                size="md"
                type="submit"
              >
                {t('accountPage.resume.preview')}
              </Button>
            </Group>
          </Stack>
        </form>
      </Paper>

      <Paper withBorder radius="md" p="lg">

        <Stack gap="md">
          <Title order={3}>{t('accountPage.resume.preview')}</Title>
          <Text c="dimmed">{previewData?.header}</Text>

          <Text>{previewData?.summary}</Text>

          <Text>
            <Text span c="dimmed">
              {t('accountPage.resume.skills')}
              :
            </Text>
            {' '}
            {previewData?.skills}
          </Text>

          <Text>
            <Text span c="dimmed">
              {t('accountPage.resume.experience')}
              :
            </Text>
            {' '}
            {previewData?.experience}
          </Text>
        </Stack>

      </Paper>
    </SimpleGrid>
  )
}
