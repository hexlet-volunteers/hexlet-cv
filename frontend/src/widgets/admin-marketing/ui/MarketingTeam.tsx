import React from 'react'
import { useTranslation } from 'react-i18next'
import { Table } from '@mantine/core'
import { MarketingTable } from './MarketingTable'
import { TableCheckbox, TableText, TableTh } from '@shared/ui/Table'

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
            <TableTh w="26%">{t('adminPage.marketing.team.author')}</TableTh>
            <TableTh w="26%">{t('adminPage.marketing.team.siteRole')}</TableTh>
            <TableTh w="26%">{t('adminPage.marketing.team.systemRole')}</TableTh>
            <TableTh w="20%" ta="center">{t('adminPage.marketing.team.onHomepage')}</TableTh>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {team?.map(member => (
            <Table.Tr key={member.id}>
              <TableText value={`${member.firstName} ${member.lastName}`} />
              <TableText value={member.position} />
              <TableText value={member.memberType} />
              <TableCheckbox
                checked={member.showOnHomepage}
                ariaLabel={t('adminPage.marketing.aria.showOnHomepage', { title: `${member.firstName} ${member.lastName}` })}
                readOnly
              />
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </MarketingTable>
  )
}
