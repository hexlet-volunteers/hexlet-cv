import React from 'react'
import { Table, Anchor, Checkbox } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { MarketingTable } from './MarketingTable'

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
            <Table.Th fz="md" py="xs" w="30%">{t('adminPage.marketing.stories.title')}</Table.Th>
            <Table.Th fz="md" py="xs" w="15%" ta="center">{t('adminPage.marketing.stories.onHomepage')}</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {stories?.map(story => (
            <Table.Tr key={story.id}>
              <Table.Td>
                <Anchor
                  size="md"
                  underline="not-hover"
                >
                  {story.title}
                </Anchor>
              </Table.Td>
              <Table.Td ta="center">
                <Checkbox
                  checked={story.showOnHomepage}
                  size="xs"
                  readOnly
                  aria-label={t('adminPage.marketing.aria.showOnHomepage', { title: story.title })}
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
