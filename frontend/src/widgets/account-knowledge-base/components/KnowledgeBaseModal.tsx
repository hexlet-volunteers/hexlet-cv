import { Modal, ScrollArea } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import type { KnowledgeBaseDTO } from '@entities/account-knowledge-base'
import { KnowledgeBaseList } from '@widgets/account-knowledge-base'

/**
 * Интерфейс пропсов компонента KnowledgeBaseModal.
 *
 * @prop knowledgebase - массив статей или видео для журнала базы знаний пользователя.
 * @prop cardsToShow - число отображаемых карточек.
 * @prop opened - состояние модального окна (открыто/закрыто).
 * @prop onClose - функция для закрытия модального кона.
 */
interface KnowledgeBaseModalProps {
  knowledgebase?: KnowledgeBaseDTO[],
  cardsToShow?: number,
  opened: boolean,
  onClose: () => void,
}

/**
 * Модальное окно для отображения всех карточек базы знаний пользователя.
 * Рендерит компонент KnowledgeBaseList, который отображает все карточки базы знаний пользователя.
 *
 * @param props - список статей или видео для базы знаний пользователя, количество отображаемых карточек, состояние модального окна, функция для закрытия модального окна { knowledgebase, cardsToShow, opened, onClose }.
 */
export const KnowledgeBaseModal: React.FC<KnowledgeBaseModalProps> = (props) => {
  const { t } = useTranslation()
  const { knowledgebase, cardsToShow, opened, onClose } = props
  return (
    <Modal
      opened={opened}
      onClose={onClose}
      size="100%"
      title={t('accountPage.knowledgeBase.modalTitle')}
      centered
      radius="md"
      scrollAreaComponent={ScrollArea.Autosize}
      py="md"
      >
        <KnowledgeBaseList knowledgebase={knowledgebase} cardsToShow={cardsToShow}/>
    </Modal>
  )
}