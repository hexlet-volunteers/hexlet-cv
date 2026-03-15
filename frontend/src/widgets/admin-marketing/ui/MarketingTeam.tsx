import React from 'react'
import { useTranslation } from 'react-i18next'
import { Table, Checkbox, Text } from '@mantine/core'
import { MarketingTable } from './MarketingTable'

export type MarketingTeamDTO = {
  id: number
  firstName: string
  lastName: string
  position: string
  memberType: string
  avatarUrl: string
  isPublished: boolean
  showOnHomepage: boolean
  displayOrder: number
  publishedAt?: string | null
  createdAt?: string
  updatedAt?: string
}

export interface TProps {
  team: MarketingTeamDTO[]
}

export const MarketingTeam: React.FC<TProps> = (props) => {
  const { team } = props
  const { t } = useTranslation()

  return (
    <MarketingTable data={team}>
      <Table withTableBorder verticalSpacing="md">
        <Table.Thead>
          <Table.Tr>
            <Table.Th fz="md" py="xs" w="26%">{t('adminPage.marketing.team.author')}</Table.Th>
            <Table.Th fz="md" py="xs" w="26%">{t('adminPage.marketing.team.siteRole')}</Table.Th>
            <Table.Th fz="md" py="xs" w="26%">{t('adminPage.marketing.team.systemRole')}</Table.Th>
            <Table.Th fz="md" py="xs" w="20%" ta="center">{t('adminPage.marketing.team.onHomepage')}</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {team?.map(member => (
            <Table.Tr key={member.id}>
              <Table.Td>
                <Text
                  size="md"
                >
                  {`${member.firstName} ${member.lastName}`}
                </Text>
              </Table.Td>
              <Table.Td>
                <Text
                  size="md"
                >
                  {member.position}
                </Text>
              </Table.Td>
              <Table.Td>
                <Text
                  size="md"
                >
                  {member.memberType}
                </Text>
              </Table.Td>
              <Table.Td ta="center">
                <Checkbox
                  checked={member.showOnHomepage}
                  size="xs"
                  readOnly
                  aria-label={t('adminPage.marketing.aria.showOnHomepage', { title: `${member.firstName} ${member.lastName}` })}
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
