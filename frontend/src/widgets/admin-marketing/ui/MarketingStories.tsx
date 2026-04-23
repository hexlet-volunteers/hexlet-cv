import React from 'react'
import { Table, Anchor, Checkbox } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { MarketingTable } from './MarketingTable'

/**
 * Тип данных для истории (story) в маркетинговом разделе.
 */
export type MarketingStoriesDTO = {
  /** Уникальный идентификатор истории. */
  id: number
  /** Заголовок истории. */
  title: string
  /** Содержимое истории. */
  content: string
  /** URL изображения истории. */
  imageUrl: string
  /** Опубликована ли история. */
  isPublished: boolean
  /** Отображать ли историю на главной странице. */
  showOnHomepage: boolean
  /** Порядок отображения истории. */
  displayOrder: number
  /** Дата публикации (опционально). */
  publishedAt?: string | null
  /** Дата создания (опционально). */
  createdAt?: string
  /** Дата последнего обновления (опционально). */
  updatedAt?: string
}

/**
 * Пропсы для компонента MarketingStories.
 */
export interface TProps {
  /** Массив историй для отображения в таблице. */
  stories: MarketingStoriesDTO[]
}

/**
 * Компонент для отображения списка историй (stories) в админ-панели.
 * Отображает заголовок истории и статус отображения на главной странице.
 *
 * @param props - Пропсы компонента.
 * @param props.stories - Массив историй для отображения.
 * @returns React-компонент с таблицей историй.
 */
export const MarketingStories: React.FC<TProps> = (props) => {
  const { stories } = props
  const { t } = useTranslation()

  return (
    <MarketingTable data={stories}>
      <Table withTableBorder verticalSpacing="md">
        <Table.Thead>
          <Table.Tr>
            <Table.Th fz="md" py="xs" w="30%">
              {t('adminPage.marketing.stories.title')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="15%" ta="center">
              {t('adminPage.marketing.stories.onHomepage')}
            </Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {stories?.map((story) => (
            <Table.Tr key={story.id + story.title}>
              <Table.Td>
                <Anchor size="md" underline="not-hover">
                  {story.title}
                </Anchor>
              </Table.Td>
              <Table.Td ta="center">
                <Checkbox
                  checked={story.showOnHomepage}
                  size="xs"
                  readOnly
                  aria-label={t('adminPage.marketing.aria.showOnHomepage', {
                    title: story.title,
                  })}
                  style={{ display: 'inline-block' }}
                />
              </Table.Td>
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </MarketingTable>
  )
}
