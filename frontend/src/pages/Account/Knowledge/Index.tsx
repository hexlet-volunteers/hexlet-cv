import { AppLayout } from '../components/AppLayout'
import { PageHeader } from '@widgets/page-header'
import { Container, Button } from '@mantine/core'
import { IconFileText } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import { KnowledgeBaseList } from '@widgets/account-knowledge-base'
import type { InertiaPage } from '@shared/types/inertia'
import type { KnowledgeBaseDTO } from '@entities/account-knowledge-base'
import { KnowledgeBaseModal } from '@widgets/account-knowledge-base'
import { useDisclosure } from '@mantine/hooks'

/**
 * Тип пропсов страницы "База знаний".
 */
interface KnowledgeBaseProps {
  knowledgebase?: KnowledgeBaseDTO[]
}

/**
 * Страница "База знаний" в личном кабинете пользователя.
 *
 * @param props - пропсы страницы, приходящие с бэкенда, в том числе список статей и видео { knowledgebase: [...] }
 * @returns React-компонент
 */
// TODO:  воможно, необходимо настроить роут для перехода на эту страницу с домашней.
const KnowledgeBase: InertiaPage<KnowledgeBaseProps> = ({
  knowledgebase,
}): JSX.Element => {
  const { t } = useTranslation()
  const [opened, { open, close }] = useDisclosure(false);

  return (
    <Container fluid>
      <PageHeader
        icon={<IconFileText />}
        title={t('accountPage.knowledgeBase.title')}
        actionButton={<Button radius="md" variant="default" onClick={open}>{t('accountPage.knowledgeBase.openJournalButton')}</Button>}
      />
      <KnowledgeBaseModal knowledgebase={knowledgebase} cardsToShow={knowledgebase?.length} opened={opened} onClose={close}/>
      <KnowledgeBaseList knowledgebase={knowledgebase} cardsToShow={9}/>
    </Container>
  )
}

KnowledgeBase.layout = (page: React.ReactNode) => <AppLayout>{page}</AppLayout>

export default KnowledgeBase