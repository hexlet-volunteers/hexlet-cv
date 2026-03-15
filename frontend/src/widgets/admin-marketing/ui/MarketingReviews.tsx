import React from 'react'
import { Table, Checkbox, Text } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { MarketingTable } from './MarketingTable'

export type MarketingReviewsDTO = {
  id: number
  author: string
  content: string
  avatarUrl: string
  isPublished: boolean
  showOnHomepage: boolean
  displayOrder: number
  publishedAt?: string | null
  createdAt?: string
  updatedAt?: string
}

export interface TProps {
  reviews: MarketingReviewsDTO[]
}

export const MarketingReviews: React.FC<TProps> = (props) => {
  const { reviews } = props
  const { t } = useTranslation()

  return (
    <MarketingTable data={reviews}>
      <Table withTableBorder verticalSpacing="md">
        <Table.Thead>
          <Table.Tr>
            <Table.Th fz="md" py="xs" w="30%">{t('adminPage.marketing.reviews.author')}</Table.Th>
            <Table.Th fz="md" py="xs" w="30%">{t('adminPage.marketing.reviews.text')}</Table.Th>
            <Table.Th fz="md" py="xs" w="15%" ta="center">{t('adminPage.marketing.reviews.inBlog')}</Table.Th>
            <Table.Th fz="md" py="xs" w="15%" ta="center">{t('adminPage.marketing.reviews.onHomepage')}</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {reviews?.map(review => (
            <Table.Tr key={review.id}>
              <Table.Td>
                <Text
                  size="md"
                >
                  {review.author}
                </Text>
              </Table.Td>
              <Table.Td>
                <Text
                  size="md"
                >
                  {review.content}
                </Text>
              </Table.Td>
              <Table.Td ta="center">
                <Checkbox
                  checked={review.isPublished}
                  size="xs"
                  readOnly
                  aria-label={t('adminPage.marketing.aria.published', { title: review.content })}
                  style={{ display: 'inline-block' }}
                />
              </Table.Td>
              <Table.Td ta="center">
                <Checkbox
                  checked={review.showOnHomepage}
                  size="xs"
                  readOnly
                  aria-label={t('adminPage.marketing.aria.showOnHomepage', { title: review.content })}
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
