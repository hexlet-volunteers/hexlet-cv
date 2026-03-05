import { Table } from '@mantine/core'
import React from 'react'
import { useTranslation } from 'react-i18next'
import { MarketingTable } from './MarketingTable'
import { TableCheckbox, TableText, TableTh } from '@shared/ui/Table'

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
            <TableTh w="30%">{t('adminPage.marketing.reviews.author')}</TableTh>
            <TableTh w="30%">{t('adminPage.marketing.reviews.text')}</TableTh>
            <TableTh w="15%" ta="center">{t('adminPage.marketing.reviews.inBlog')}</TableTh>
            <TableTh w="15%" ta="center">{t('adminPage.marketing.reviews.onHomepage')}</TableTh>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {reviews?.map(review => (
            <Table.Tr key={review.id}>
              <TableText value={review.author} />
              <TableText value={review.content} />
              <TableCheckbox
                checked={review.isPublished}
                ariaLabel={t('adminPage.marketing.aria.published', { title: review.content })}
                readOnly
              />
              <TableCheckbox
                checked={review.showOnHomepage}
                ariaLabel={t('adminPage.marketing.aria.showOnHomepage', { title: review.content })}
                readOnly
              />
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </MarketingTable>
  )
}
