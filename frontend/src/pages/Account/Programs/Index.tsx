import { AppLayout } from '../components/AppLayout'
import { PageHeader } from '@widgets/page-header'
import { Container, ScrollArea } from '@mantine/core'
import { IconSchool } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import { StudyProgramsList } from '@widgets/account-study-programs'
import type { InertiaPage } from '@shared/types/inertia'
import type { StudyProgramDTO } from '@widgets/account-study-programs/types'

/**
 * Тип пропсов страницы "Программы обучения".
 */
interface StudyProgramsProps {
  programs?: StudyProgramDTO[]
}

/**
 * Страница "Программы обучения" в личном кабинете пользователя.
 *
 * @param props - пропсы страницы, приходящие с бэкенда, в том числе список программ обучения { programs: [...] }
 * @returns React-компонент
 */
const StudyPrograms: InertiaPage<StudyProgramsProps> = ({
  programs,
}): JSX.Element => {
  const { t } = useTranslation()

  return (
    <Container fluid>
      <PageHeader
        icon={<IconSchool />}
        title={t('accountPage.studyPrograms.title')}
      />
      <ScrollArea h={600}>
        <StudyProgramsList programs={programs} />
      </ScrollArea>
    </Container>
  )
}

StudyPrograms.layout = (page: React.ReactNode) => <AppLayout>{page}</AppLayout>

export default StudyPrograms
