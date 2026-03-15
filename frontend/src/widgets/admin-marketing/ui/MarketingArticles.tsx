import React from 'react'
import { Table, Anchor, Checkbox } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { MarketingTable } from './MarketingTable'
// import { TableTh } from '@shared/ui/Table'

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
            <Table.Th fz="md" py="xs" w="30%">{t('adminPage.marketing.articles.title')}</Table.Th>
            <Table.Th fz="md" py="xs" w="15%" ta="center">{t('adminPage.marketing.articles.inBlog')}</Table.Th>
            <Table.Th fz="md" py="xs" w="15%" ta="center">{t('adminPage.marketing.articles.onHomepage')}</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {articles?.map(article => (
            <Table.Tr key={article.id}>
              <Table.Td>
                <Anchor
                  size="md"
                  underline="not-hover"
                >
                  {article.title}
                </Anchor>
              </Table.Td>
              <Table.Td ta="center">
                <Checkbox
                  checked={article.isPublished}
                  size="xs"
                  readOnly
                  aria-label={t('adminPage.marketing.aria.published', { title: article.title })}
                  style={{ display: 'inline-block' }}
                />
              </Table.Td>
              <Table.Td ta="center">
                <Checkbox
                  checked={article.showOnHomepage}
                  size="xs"
                  readOnly
                  aria-label={t('adminPage.marketing.aria.showOnHomepage', { title: article.title })}
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
