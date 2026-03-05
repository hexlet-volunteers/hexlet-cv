import { Table } from '@mantine/core'
import React from 'react'
import { useTranslation } from 'react-i18next'
import { MarketingTable } from './MarketingTable'
import { TableAnchor, TableCheckbox, TableTh } from '@shared/ui/Table'

export type MarketingStoriesDTO = {
  id: number
  title: string
  content: string
  imageUrl: string
  isPublished: boolean
  showOnHomepage: boolean
  displayOrder: number
  publishedAt?: string | null
  createdAt?: string
  updatedAt?: string
}

export interface TProps {
  stories: MarketingStoriesDTO[]
}

export const MarketingStories: React.FC<TProps> = (props) => {
  const { stories } = props
  const { t } = useTranslation()

  return (
    <MarketingTable data={stories}>
      <Table withTableBorder verticalSpacing="md">
        <Table.Thead>
          <Table.Tr>
            <TableTh w="30%">{t('adminPage.marketing.stories.title')}</TableTh>
            <TableTh w="15%" ta="center">{t('adminPage.marketing.stories.onHomepage')}</TableTh>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {stories?.map(story => (
            <Table.Tr key={story.id}>
              <TableAnchor underline="not-hover">
                {story.title}
              </TableAnchor>
              <TableCheckbox
                checked={story.showOnHomepage}
                ariaLabel={t('adminPage.marketing.aria.showOnHomepage', { title: story.title })}
                readOnly
              />
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </MarketingTable>
  )
}
