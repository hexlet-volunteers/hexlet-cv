import React from 'react'
import { Table } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { MarketingTable } from './MarketingTable'
import { TableAnchor, TableCheckbox, TableTh } from '@shared/ui/Table'

export type MarketingArticlesDTO = {
  id: number
  title: string
  content: string
  imageUrl: string
  author: string
  readingTime: number
  isPublished: boolean
  homeComponentId?: string | null
  showOnHomepage: boolean
  displayOrder: number
  publishedAt?: string | null
  createdAt?: string
  updatedAt?: string
}

export interface TProps {
  articles: MarketingArticlesDTO[]
}

export const MarketingArticles: React.FC<TProps> = (props) => {
  const { articles } = props
  const { t } = useTranslation()

  return (
    <MarketingTable data={articles}>
      <Table withTableBorder verticalSpacing="md">
        <Table.Thead>
          <Table.Tr>
            <TableTh w="30%">{t('adminPage.marketing.articles.title')}</TableTh>
            <TableTh w="15%" ta="center">{t('adminPage.marketing.articles.inBlog')}</TableTh>
            <TableTh w="15%" ta="center">{t('adminPage.marketing.articles.onHomepage')}</TableTh>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {articles?.map(article => (
            <Table.Tr key={article.id}>
              <TableAnchor underline="not-hover">
                {article.title}
              </TableAnchor>
              <TableCheckbox
                checked={article.isPublished}
                ariaLabel={t('adminPage.marketing.aria.published', { title: article.title })}
                readOnly
              />
              <TableCheckbox
                checked={article.showOnHomepage}
                ta="center"
                ariaLabel={t('adminPage.marketing.aria.showOnHomepage', { title: article.title })}
                readOnly
              />
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </MarketingTable>
  )
}
