import type { InertiaPage } from '@shared/types/inertia'
import { AppLayout } from '../components/AppLayout'
import { CoverLetter, downloadJson } from '@widgets/coverLetter'
import { useTranslation } from 'react-i18next'
import { PageHeader } from '@widgets/page-header'
import { IconPencil } from '@tabler/icons-react'
import { Group, Button } from '@mantine/core'
import { useState } from 'react'
import type { TCoverLetterData } from '@widgets/coverLetter/types'

const CoverLetterInfo: InertiaPage = () => {
  const { t } = useTranslation()
  const [previewData, setPreviewData] = useState<TCoverLetterData | null>(null)
  return (
    <>
      <Group justify="space-between" align="center" mb="md">
        <PageHeader
          icon={<IconPencil />}
          title={t('accountPage.coverLetter.title')}
        />
        <Button
          variant="filled"
          color="yellow"
          radius="md"
          size="md"
          type="button"
          onClick={() => previewData && downloadJson(previewData)}
        >
          {t('accountPage.coverLetter.download')}
        </Button>
      </Group>
      <CoverLetter onPreview={setPreviewData} previewData={previewData} />
    </>
  )
}

CoverLetterInfo.layout = (page) => <AppLayout>{page}</AppLayout>
export default CoverLetterInfo
