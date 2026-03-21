import type React from 'react'
import { useTranslation } from 'react-i18next'
import { useState } from 'react'
import { type UserDTO, EUserRole, getSubscriptionStatus } from '@entities/user'
import {
  Container, Input, Title, Group, Chip,
  Table, Text, Select, Badge, Button
} from '@mantine/core'

type ActiveTab = 'registered' | 'active' | 'expired' | 'admin'

function formatDate(date?: string): string | null {
  if (!date) return null

  const d = new Date(date)
  return isNaN(d.getTime()) ? null : d.toLocaleDateString()
}

function filterUsers(users: Array<UserDTO>, activeTab: ActiveTab, searchQuery: string): UserDTO[] {
  if (!users?.length) {
    return []
  }

  const filtered = (() => {
    switch (activeTab) {
      case 'registered':
        return users
      case 'active':
        return users.filter(user => getSubscriptionStatus(user) === 'active')
      case 'expired':
        return users.filter(user => getSubscriptionStatus(user) === 'expired')
      case 'admin':
        return users.filter(user => user.role === EUserRole.ADMIN)
      default:
        return []
    }
  })()

  const query = searchQuery.trim().toLowerCase()

  if (query) {
    return filtered.filter((user) => {
      const email = user.email?.trim().toLowerCase()
      const name = user.name?.trim().toLowerCase()
      const login = user.login?.trim().toLowerCase()

      return email.includes(query)
        || name.includes(query)
        || login.includes(query)
    })
  }

  return filtered
}

interface AdminUsersProps {
  users: UserDTO[]
}

export const AdminUsers: React.FC<AdminUsersProps> = (props) => {
  const { users } = props
  const { t } = useTranslation()
  const [activeTab, setActiveTab] = useState<ActiveTab>('registered')
  const [searchQuery, setSearchQuery] = useState<string>('')
  const selectData = Object.values(EUserRole).map(role => ({
    value: role,
    label: t(`adminPage.users.roles.${role}`),
  }))

  const handleTabChange = (value: string | null) => {
    if (!value) return
    const tab = value as ActiveTab
    setActiveTab(tab)
  }

  // Здесь должен быть обработчик для изменения селектов

  function renderDate(user: UserDTO): string | null {
    const status = getSubscriptionStatus(user)

    if (status === 'none') return null
    const startDate = formatDate(user.startsAt || '')
    const endDate = formatDate(user.endsAt || '')

    return `${t('adminPage.users.sub.startsAt')}: ${startDate} - ${t('adminPage.users.sub.endsAt')}: ${endDate}`
  }
  // Логину на кнопки пока не добавлял

  function renderButton(user: UserDTO): JSX.Element {
    const status = getSubscriptionStatus(user)

    return (
      <Button variant="outline" color="gray" ml="sm">
        {status === 'active'
          ? t('adminPage.users.sub.complete')
          : t('adminPage.users.sub.add')}
      </Button>
    )
  }

  function renderBadge(user: UserDTO): JSX.Element {
    const status = getSubscriptionStatus(user)
    const tarif = user?.tarif

    if (status === 'none') return (
      <Badge color="gray" variant="light" fw={500}>{t('adminPage.users.sub.noSub')}</Badge>
    )

    const badge = getSubscriptionStatus(user) === 'active'
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
          value={user.role}
          data={selectData}
          // Здесь добавить обработчик для изменения роли.
        />
      </Table.Td>
      <Table.Td>
        {renderBadge(user)}
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
        mb="lg"
      >
        {t('adminPage.users.title')}
      </Title>
      <Group>
        <Chip.Group multiple={false} value={activeTab} onChange={handleTabChange}>
          <Chip value="registered">
            {t('adminPage.users.filters.registered')}
            (
            {filterUsers(users, 'registered', '').length}
            )
          </Chip>
          <Chip value="active">
            {t('adminPage.users.filters.subscription')}
            (
            {filterUsers(users, 'active', '').length}
            )
          </Chip>
          <Chip value="expired">
            {t('adminPage.users.filters.expired')}
            (
            {filterUsers(users, 'expired', '').length}
            )
          </Chip>
          <Chip value="admin">
            {t('adminPage.users.filters.admins')}
            (
            {filterUsers(users, 'admin', '').length}
            )
          </Chip>
        </Chip.Group>
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
    </Container>
  )
}
