import {
  SimpleGrid,
  Center,
  Loader,
} from '@mantine/core'
import { EmptyPlaceholder } from '@shared/ui'
import { IconFilesOff } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import type { KnowledgeBaseDTO } from '@entities/account-knowledge-base'
import { KnowledgeBaseCard } from '@entities/account-knowledge-base'

/**
 * Интерфейс пропсов компонента KnowledgeBase.
 *
 * @prop knowledgebase - массив статей или видео для базы знаний пользователя.
 * @prop cardsToShow - число отображаемых карточек.
 */
interface KnowledgeBaseListProps {
  knowledgebase?: KnowledgeBaseDTO[],
  cardsToShow?: number,
}

/**
 * Виджет для отображения первых девяти карточек базы знаний пользователя.
 * Рендерит сетку из 9 карточек с названием статьи или видео, их продолжительностью и кнопкой "Читать" с ссылкой на материал.
 * Если данные не пришли или undefined - показывает лоадер, если массив данных пустой - показывает плейсхолдер пустого списка.
 *
 * @param props - список статей или видео для базы знаний пользователя, количество отображаемых карточек { knowledgebase: [...], cardToShow: ... }.
 */
export const KnowledgeBaseList: React.FC<KnowledgeBaseListProps> = (props) => {
  const { knowledgebase, cardsToShow } = props
  const { t } = useTranslation()

  if (!knowledgebase) {
    return (
      <Center h={200}>
        <Loader color="blue" size="lg" />
      </Center>
    )
  }

  if (!knowledgebase.length) {
    return (
      <EmptyPlaceholder
        title={t('accountPage.knowledgeBase.baseIsEmpty')}
        icon={IconFilesOff}
      />
    )
  }

  return (
    <SimpleGrid spacing="lg" cols={{ base: 1, sm: 2, lg: 3 }}>
      {knowledgebase.slice(0, cardsToShow).map((item) => (
        <KnowledgeBaseCard key={item.id} card={item} />
      ))}
    </SimpleGrid>
  )
}
