import type { InertiaPage } from '@shared/types/inertia'
import { AppLayout } from '../components/AppLayout'
import { Resume, downloadJson } from '@widgets/resume'
import { useTranslation } from 'react-i18next'
import { PageHeader } from '@widgets/page-header'
import { IconFileText } from '@tabler/icons-react'
import { Group, Button } from '@mantine/core'
import { useState } from 'react'
import type { TResumeData } from '@widgets/resume/types'

const ResumeInfo: InertiaPage = () => {
  const { t } = useTranslation()
  const [previewData, setPreviewData] = useState<TResumeData | null>(null)
  return (
    <>
      <Group justify="space-between" align="center" mb="md">
        <PageHeader
          icon={<IconFileText />}
          title={t('accountPage.resume.title')}
        />
        <Button
          variant="filled"
          color="yellow"
          radius="md"
          size="md"
          type="button"
          onClick={() => previewData && downloadJson(previewData)}
        >
          {t('accountPage.resume.download')}
        </Button>
      </Group>
      <Resume onPreview={setPreviewData} previewData={previewData} />
    </>
  )
}

ResumeInfo.layout = page => <AppLayout>{page}</AppLayout>
export default ResumeInfo
