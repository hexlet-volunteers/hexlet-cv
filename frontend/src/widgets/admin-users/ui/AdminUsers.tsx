import type React from 'react'
import { ROLES, type UserRole } from './userRoles'
import { useTranslation } from 'react-i18next'
import { useState, useMemo } from 'react'
import {
  Container,
  Input,
  Tabs,
  Title,
  Group,
  Table,
  Text,
  Select,
  Pill,
  Button
} from '@mantine/core'

type ISODateString = `${number}-${number}-${number}T${number}:${number}:${number}Z` | null

export type UsersDTO = {
  id: number
  name: string
  email: string
  login: string
  role: UserRole
  isSubscribed: boolean | null
  tarif: 'pro' | null
  startsAt?: ISODateString
  endsAt?: ISODateString
}

export interface TProps {
  users: UsersDTO[]
}

function formatDate(date?: ISODateString): string {
  if (!date) return '—'

  const d = new Date(date)
  return isNaN(d.getTime()) ? 'Неверный формат даты' : d.toLocaleDateString('ru-RU')
}

function hasActiveSub(tarif: string | null, endsAt: ISODateString): boolean {
  if (!tarif || !endsAt) return false

  const expiryDate = new Date(endsAt).getTime()
  const now = Date.now()

  return expiryDate > now
}

function filtredUsers(users: Array<UsersDTO>, activeTab: string | null, searchQuery: string): Array<UsersDTO> {
  if (!users) {
    return []
  }

  if (activeTab === null) {
    if (!searchQuery.trim()) return users
    const query = searchQuery.toLowerCase()
    return users.filter(user =>
      user.email?.toLowerCase().includes(query)
      || user.name?.toLowerCase().includes(query)
      || user.login?.toLowerCase().includes(query)
    )
  }

  switch (activeTab) {
    case 'registered':
      return users
    case 'withSub':
      return users.filter(user => user.endsAt && new Date(user.endsAt) > new Date())
    case 'expiredSub':
      return users.filter(user => user.endsAt && new Date(user.endsAt) < new Date())
    case 'admins':
      return users.filter(user => user.role === 'Администратор')
    default:
      return []
  }
}

export const AdminUsers: React.FC<TProps> = (props): JSX.Element => {
  const { users } = props
  const { t } = useTranslation()
  const [activeTab, setActiveTab] = useState<string | null>('registered')
  const [searchQuery, setSearchQuery] = useState<string>('')
  const selectData = Object.values(ROLES)

  const handleTabChange = (tab: string | null) => {
    setActiveTab(tab)
    setSearchQuery('')
  }

  // Здесь должен быть обработчик для изменения селектов

  function renderDate(el: UsersDTO): string | null {
    const startDate = formatDate(el.startsAt)
    const endDate = formatDate(el.endsAt)
    return el.isSubscribed || el.startsAt
      ? `${t('adminPage.users.sub.startsAt')}: ${startDate} - ${t('adminPage.users.sub.endsAt')}: ${endDate}`
      : null
  }
  // Логину на кнопки пока не добавлял

  function renderButton(el: UsersDTO): JSX.Element {
    const hasActive = hasActiveSub(el.tarif, el.endsAt ?? null)

    return (
      <Button variant="outline" color="gray" ml="sm">
        {hasActive
          ? t('adminPage.users.sub.complete')
          : t('adminPage.users.sub.add')}
      </Button>
    )
  }

  function setBadge(tarif: string | null, expired: ISODateString): JSX.Element {
    if (!expired || tarif === null) return <Pill bg="gray" fw={500} c="#fff">{t('adminPage.users.sub.noSub')}</Pill>
    const expiredDate = Number(new Date(expired))
    const dateNow = Date.now()

    const pill = expired && Number(expiredDate) > Number(dateNow)
      ? <Pill bg="green" fw={500} c="#fff">{t('adminPage.users.sub.active')}</Pill>
      : <Pill bg="red" fw={500} c="#fff">{t('adminPage.users.sub.expired')}</Pill>
    return (
      <>
        {pill}
        <Pill bg="gray" ml="sm" fw={500} c="#fff">
          {t('adminPage.users.sub.plan')}
          {tarif}
        </Pill>
      </>
    )
  }

  const rows = useMemo(() => filtredUsers(users, activeTab, searchQuery), [users, activeTab, searchQuery]).map(user => (
    <Table.Tr key={user.id}>
      <Table.Td>
        <Text>{user.name}</Text>
        <Text c="dimmed">{user.email}</Text>
      </Table.Td>
      <Table.Td>{user.login}</Table.Td>
      <Table.Td>
        <Select
          label=""
          defaultValue={user.role}
          data={selectData}
          // Здесь добавить обработчик для изменения роли.
        />
      </Table.Td>
      <Table.Td>
        {setBadge(user.tarif, user.endsAt ?? null)}
        <Text>
          {renderDate(user)}
        </Text>
      </Table.Td>
      <Table.Td>
        <Button variant="outline" color="gray" mr="sm">{t('adminPage.users.sub.profile')}</Button>
        {renderButton(user)}
      </Table.Td>
    </Table.Tr>
  ))

  return (
    <Container fluid>
      <Title
        order={2}
        fw={500}
        mt="sm"
        mr="sm"
      >
        {t('adminPage.users.title')}
      </Title>
      <Tabs value={activeTab} onChange={handleTabChange} color="grey" variant="pills" mt="lg">
        <Group>
          <Tabs.List color="purple">
            <Tabs.Tab value="registered">
              {t('adminPage.users.filters.registered')}
              (
              {useMemo(() => filtredUsers(users, 'registered', '').length, [users])}
              )
            </Tabs.Tab>
            <Tabs.Tab value="withSub">
              {t('adminPage.users.filters.subscription')}
              (
              {useMemo(() => filtredUsers(users, 'withSub', '').length, [users])}
              )
            </Tabs.Tab>
            <Tabs.Tab value="expiredSub">
              {t('adminPage.users.filters.expired')}
              (
              {useMemo(() => filtredUsers(users, 'expiredSub', '').length, [users])}
              )
            </Tabs.Tab>
            <Tabs.Tab value="admins">
              {t('adminPage.users.filters.admins')}
              (
              {useMemo(() => filtredUsers(users, 'admins', '').length, [users])}
              )
            </Tabs.Tab>
          </Tabs.List>

          <Input
            placeholder={t('adminPage.users.filters.placeholder')}
            size="xs"
            style={{ width: 200 }}
            value={searchQuery}
            onChange={(event) => {
              setActiveTab(null)
              setSearchQuery(event.currentTarget.value)
            }}
          />
        </Group>
        <Table mt="lg" withTableBorder>
          <Table.Thead style={{ backgroundColor: 'grey' }}>
            <Table.Tr>
              <Table.Th>{t('adminPage.users.table.user')}</Table.Th>
              <Table.Th>{t('adminPage.users.table.login')}</Table.Th>
              <Table.Th>{t('adminPage.users.table.role')}</Table.Th>
              <Table.Th>{t('adminPage.users.table.subscription')}</Table.Th>
              <Table.Th>{t('adminPage.users.table.actions')}</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>{rows}</Table.Tbody>
        </Table>
      </Tabs>
    </Container>
  )
}
