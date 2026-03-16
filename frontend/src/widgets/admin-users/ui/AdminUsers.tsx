import type React from 'react'
import { useTranslation } from 'react-i18next'
import { useState } from 'react'
import { type UserDTO, EUserRole, getSubscriptionStatus } from '@entities/user'
import {
  Container, Input, Tabs, Title, Group,
  Table, Text, Select, Badge, Button
} from '@mantine/core'

function formatDate(date?: string): string | null {
  if (!date) return null

  const d = new Date(date)
  return isNaN(d.getTime()) ? null : d.toLocaleDateString()
}

function filterUsers(users: Array<UserDTO>, activeTab: string | null, searchQuery: string): UserDTO[] {
  const now = new Date()

  if (!users?.length) {
    return []
  }

  const filtered = (() => {
    switch (activeTab) {
      case 'registered':
        return users
      case 'withSub':
        return users.filter(user => user.endsAt && new Date(user.endsAt) > now)
      case 'expiredSub':
        return users.filter(user => user.endsAt && new Date(user.endsAt) < now)
      case 'admins':
        return users.filter(user => user.role === EUserRole.ADMIN)
      default:
        return users
    }
  })()

  if (searchQuery.trim()) {
    const query = searchQuery.toLowerCase()
    return filtered.filter(user =>
      user.email?.toLowerCase().includes(query)
      || user.name?.toLowerCase().includes(query)
      || user.login?.toLowerCase().includes(query)
    )
  }

  return filtered
}

interface AdminUsersProps {
  users: UserDTO[]
}

export const AdminUsers: React.FC<AdminUsersProps> = (props) => {
  const { users } = props
  const { t } = useTranslation()
  const [activeTab, setActiveTab] = useState<string | null>('registered')
  const [searchQuery, setSearchQuery] = useState<string>('')
  const selectData = Object.values(EUserRole).map(role => ({
    value: role,
    label: t(`adminPage.users.roles.${role}`),
  }))

  const handleTabChange = (tab: string | null) => {
    setActiveTab(tab)
    setSearchQuery('')
  }

  // Здесь должен быть обработчик для изменения селектов

  function renderDate(user: UserDTO): string | null {
    const status = getSubscriptionStatus(user)

    if (status === 'none') return null
    const startDate = formatDate(user.startsAt || '')
    const endDate = formatDate(user.endsAt || '')

    if (status === 'active') {
      return `${t('adminPage.users.sub.startsAt')}: ${startDate} - ${t('adminPage.users.sub.endsAt')}: ${endDate}`
    }

    if (status === 'expired') {
      return `${t('adminPage.users.sub.startsAt')}: ${startDate} - ${t('adminPage.users.sub.endsAt')}: ${endDate}`
    }

    return null
  }
  // Логину на кнопки пока не добавлял

  function renderButton(user: UserDTO): JSX.Element | null {
    const status = getSubscriptionStatus(user)

    return (
      <Button variant="outline" color="gray" ml="sm">
        {status === 'active'
          ? t('adminPage.users.sub.complete')
          : t('adminPage.users.sub.add')}
      </Button>
    )
  }

  function renderBadge(tarif: string | null, expired?: string): JSX.Element {
    if (!expired || tarif === null) return <Badge color="gray" variant="light" fw={500}>{t('adminPage.users.sub.noSub')}</Badge>
    const expiredDate = Number(new Date(expired))
    const dateNow = Date.now()

    const badge = expired && Number(expiredDate) > Number(dateNow)
      ? <Badge color="green" variant="light" fw={500}>{t('adminPage.users.sub.active')}</Badge>
      : <Badge color="red" variant="light" fw={500}>{t('adminPage.users.sub.expired')}</Badge>
    return (
      <>
        {badge}
        <Badge bg="gray" ml="sm" fw={500}>
          {t('adminPage.users.sub.plan')}
          {tarif}
        </Badge>
      </>
    )
  }

  const rows = filterUsers(users, activeTab, searchQuery).map(user => (
    <Table.Tr key={user.id}>
      <Table.Td>
        <Text>{user.name}</Text>
        <Text c="dimmed">{user.email}</Text>
      </Table.Td>
      <Table.Td>{user.login}</Table.Td>
      <Table.Td>
        <Select
          label=""
          value={user.role}
          data={selectData}
          // Здесь добавить обработчик для изменения роли.
        />
      </Table.Td>
      <Table.Td>
        {renderBadge(user.tarif, user.endsAt || '')}
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
              {filterUsers(users, 'registered', '').length}
              )
            </Tabs.Tab>
            <Tabs.Tab value="withSub">
              {t('adminPage.users.filters.subscription')}
              (
              {filterUsers(users, 'withSub', '').length}
              )
            </Tabs.Tab>
            <Tabs.Tab value="expiredSub">
              {t('adminPage.users.filters.expired')}
              (
              {filterUsers(users, 'expiredSub', '').length}
              )
            </Tabs.Tab>
            <Tabs.Tab value="admins">
              {t('adminPage.users.filters.admins')}
              (
              {filterUsers(users, 'admins', '').length}
              )
            </Tabs.Tab>
          </Tabs.List>

          <Input
            placeholder={t('adminPage.users.filters.placeholder')}
            size="xs"
            style={{ width: 200 }}
            value={searchQuery}
            onChange={(event) => {
              setSearchQuery(event.currentTarget.value)
            }}
          />
        </Group>
        <Table mt="lg" withTableBorder>
          <Table.Thead>
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
