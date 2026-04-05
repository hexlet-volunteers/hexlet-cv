import type { InertiaPage } from '@shared/types/inertia'
import { AppLayout } from '../components/AppLayout'
import { useTranslation } from 'react-i18next'
import { PageHeader } from '@widgets/page-header'
import { IconPencil } from '@tabler/icons-react'
import {
  CoverLetterProvider,
  CoverLetterExportButton,
  CoverLetterEditor,
  CoverLetterPreview,
} from '@widgets/cover-letter'
import { type TCoverLetterData } from '@widgets/cover-letter/types'
import { SimpleGrid } from '@mantine/core'
import { usePage } from '@inertiajs/react'
import type { PageProps } from '@inertiajs/core'

interface CoverLetterInfoProps {
  coverLetter: TCoverLetterData
}

const CoverLetterInfo: InertiaPage<CoverLetterInfoProps> = () => {
  const { props } = usePage<PageProps & { coverLetter: TCoverLetterData }>()

  const { t } = useTranslation()
  return (
    <CoverLetterProvider initialData={props.coverLetter}>
      <PageHeader
        icon={<IconPencil />}
        title={t('accountPage.coverLetter.title')}
        actionButton={<CoverLetterExportButton />}
      />
      <SimpleGrid cols={2} spacing="lg">
        <CoverLetterEditor />
        <CoverLetterPreview />
      </SimpleGrid>
    </CoverLetterProvider>
  )
}

CoverLetterInfo.layout = (page) => <AppLayout>{page}</AppLayout>
export default CoverLetterInfo
