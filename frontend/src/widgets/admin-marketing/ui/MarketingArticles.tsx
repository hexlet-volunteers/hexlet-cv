import React from 'react'
import { Table, Anchor, Checkbox } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { MarketingTable } from './MarketingTable'

/**
 * Тип данных для статьи в маркетинговом разделе.
 */
export type MarketingArticlesDTO = {
  /** Уникальный идентификатор статьи. */
  id: number
  /** Заголовок статьи. */
  title: string
  /** Содержимое статьи. */
  content: string
  /** URL изображения статьи. */
  imageUrl: string
  /** Автор статьи. */
  author: string
  /** Время чтения в минутах. */
  readingTime: number
  /** Опубликована ли статья. */
  isPublished: boolean
  /** Идентификатор компонента на главной странице (опционально). */
  homeComponentId?: string | null
  /** Отображать ли статью на главной странице. */
  showOnHomepage: boolean
  /** Порядок отображения статьи. */
  displayOrder: number
  /** Дата публикации (опционально). */
  publishedAt?: string | null
  /** Дата создания (опционально). */
  createdAt?: string
  /** Дата последнего обновления (опционально). */
  updatedAt?: string
}

/**
 * Пропсы для компонента MarketingArticles.
 */
export interface TProps {
  /** Массив статей для отображения в таблице. */
  articles: MarketingArticlesDTO[]
}

/**
 * Компонент для отображения списка статей в админ-панели.
 * Отображает заголовок статьи, статус публикации и отображения на главной странице.
 *
 * @param props - Пропсы компонента.
 * @param props.articles - Массив статей для отображения.
 * @returns React-компонент с таблицей статей.
 */
export const MarketingArticles: React.FC<TProps> = (props) => {
  const { articles } = props
  const { t } = useTranslation()

  return (
    <MarketingTable data={articles}>
      <Table withTableBorder verticalSpacing="md">
        <Table.Thead>
          <Table.Tr>
            <Table.Th fz="md" py="xs" w="30%">
              {t('adminPage.marketing.articles.title')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="15%" ta="center">
              {t('adminPage.marketing.articles.inBlog')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="15%" ta="center">
              {t('adminPage.marketing.articles.onHomepage')}
            </Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {articles?.map((article) => (
            <Table.Tr key={article.id + article.title}>
              <Table.Td>
                <Anchor size="md" underline="not-hover">
                  {article.title}
                </Anchor>
              </Table.Td>
              <Table.Td ta="center">
                <Checkbox
                  checked={article.isPublished}
                  size="xs"
                  readOnly
                  aria-label={t('adminPage.marketing.aria.published', {
                    title: article.title,
                  })}
                  style={{ display: 'inline-block' }}
                />
              </Table.Td>
              <Table.Td ta="center">
                <Checkbox
                  checked={article.showOnHomepage}
                  size="xs"
                  readOnly
                  aria-label={t('adminPage.marketing.aria.showOnHomepage', {
                    title: article.title,
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
