import React from 'react'
import { Table, Checkbox, Text } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { MarketingTable } from './MarketingTable'

/**
 * Тип данных для отзыва в маркетинговом разделе.
 */
export type MarketingReviewsDTO = {
  /** Уникальный идентификатор отзыва. */
  id: number
  /** Автор отзыва. */
  author: string
  /** Текст отзыва. */
  content: string
  /** URL аватара автора отзыва. */
  avatarUrl: string
  /** Опубликован ли отзыв. */
  isPublished: boolean
  /** Отображать ли отзыв на главной странице. */
  showOnHomepage: boolean
  /** Порядок отображения отзыва. */
  displayOrder: number
  /** Дата публикации (опционально). */
  publishedAt?: string | null
  /** Дата создания (опционально). */
  createdAt?: string
  /** Дата последнего обновления (опционально). */
  updatedAt?: string
}

/**
 * Пропсы для компонента MarketingReviews.
 */
export interface TProps {
  /** Массив отзывов для отображения в таблице. */
  reviews: MarketingReviewsDTO[]
}

/**
 * Компонент для отображения списка отзывов в админ-панели.
 * Отображает автора, текст отзыва, статус публикации и отображения на главной странице.
 *
 * @param props - Пропсы компонента.
 * @param props.reviews - Массив отзывов для отображения.
 * @returns React-компонент с таблицей отзывов.
 */
export const MarketingReviews: React.FC<TProps> = (props) => {
  const { reviews } = props
  const { t } = useTranslation()

  return (
    <MarketingTable data={reviews}>
      <Table withTableBorder verticalSpacing="md">
        <Table.Thead>
          <Table.Tr>
            <Table.Th fz="md" py="xs" w="30%">
              {t('adminPage.marketing.reviews.author')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="30%">
              {t('adminPage.marketing.reviews.text')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="15%" ta="center">
              {t('adminPage.marketing.reviews.inBlog')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="15%" ta="center">
              {t('adminPage.marketing.reviews.onHomepage')}
            </Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {reviews?.map((review) => (
            <Table.Tr key={review.id + review.author}>
              <Table.Td>
                <Text size="md">{review.author}</Text>
              </Table.Td>
              <Table.Td>
                <Text size="md">{review.content}</Text>
              </Table.Td>
              <Table.Td ta="center">
                <Checkbox
                  checked={review.isPublished}
                  size="xs"
                  readOnly
                  aria-label={t('adminPage.marketing.aria.published', {
                    title: review.content,
                  })}
                  style={{ display: 'inline-block' }}
                />
              </Table.Td>
              <Table.Td ta="center">
                <Checkbox
                  checked={review.showOnHomepage}
                  size="xs"
                  readOnly
                  aria-label={t('adminPage.marketing.aria.showOnHomepage', {
                    title: review.content,
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
