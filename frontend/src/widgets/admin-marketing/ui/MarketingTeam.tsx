import React from 'react'
import { useTranslation } from 'react-i18next'
import { Table, Checkbox, Text } from '@mantine/core'
import { MarketingTable } from './MarketingTable'

/**
 * Тип данных для участника команды в маркетинговом разделе.
 */
export type MarketingTeamDTO = {
  /** Уникальный идентификатор участника команды. */
  id: number
  /** Имя участника. */
  firstName: string
  /** Фамилия участника. */
  lastName: string
  /** Должность участника (отображаемая на сайте). */
  position: string
  /** Тип участника (системная роль). */
  memberType: string
  /** URL аватара участника. */
  avatarUrl: string
  /** Опубликован ли профиль участника. */
  isPublished: boolean
  /** Отображать ли участника на главной странице. */
  showOnHomepage: boolean
  /** Порядок отображения участника. */
  displayOrder: number
  /** Дата публикации (опционально). */
  publishedAt?: string | null
  /** Дата создания (опционально). */
  createdAt?: string
  /** Дата последнего обновления (опционально). */
  updatedAt?: string
}

/**
 * Пропсы для компонента MarketingTeam.
 */
export interface TProps {
  /** Массив участников команды для отображения в таблице. */
  team: MarketingTeamDTO[]
}

/**
 * Компонент для отображения списка участников команды в админ-панели.
 * Отображает имя, должность, системную роль и статус отображения на главной странице.
 *
 * @param props - Пропсы компонента.
 * @param props.team - Массив участников команды для отображения.
 * @returns React-компонент с таблицей участников команды.
 */
export const MarketingTeam: React.FC<TProps> = (props) => {
  const { team } = props
  const { t } = useTranslation()

  return (
    <MarketingTable data={team}>
      <Table withTableBorder verticalSpacing="md">
        <Table.Thead>
          <Table.Tr>
            <Table.Th fz="md" py="xs" w="26%">
              {t('adminPage.marketing.team.author')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="26%">
              {t('adminPage.marketing.team.siteRole')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="26%">
              {t('adminPage.marketing.team.systemRole')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="20%" ta="center">
              {t('adminPage.marketing.team.onHomepage')}
            </Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {team?.map((member) => (
            <Table.Tr key={member.id + member.firstName + member.lastName}>
              <Table.Td>
                <Text size="md">
                  {`${member.firstName} ${member.lastName}`}
                </Text>
              </Table.Td>
              <Table.Td>
                <Text size="md">{member.position}</Text>
              </Table.Td>
              <Table.Td>
                <Text size="md">{member.memberType}</Text>
              </Table.Td>
              <Table.Td ta="center">
                <Checkbox
                  checked={member.showOnHomepage}
                  size="xs"
                  readOnly
                  aria-label={t('adminPage.marketing.aria.showOnHomepage', {
                    title: `${member.firstName} ${member.lastName}`,
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
