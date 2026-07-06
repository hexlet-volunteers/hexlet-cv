import { NavLink as RouterNavLink, Outlet } from 'react-router'
import { AppShell, Box, Stack, Text, Group, Anchor } from '@mantine/core'

const adminNav = [
  { label: 'Дашборд', to: '/admin' },
  { label: 'Пользователи', to: '/admin/users' },
  { label: 'Блог', to: '/admin/blog' },
  { label: 'Моки', to: '/admin/mocks' },
  { label: 'Комм. проекты', to: '/admin/projects' },
  { label: 'Источники', to: '/admin/sources' },
]

export function AdminLayout() {
  return (
    <AppShell navbar={{ width: 240, breakpoint: 'sm' }} padding="lg">
      <AppShell.Navbar p="sm" style={{ background: '#17171E' }}>
        <Group gap={8} px="sm" py="xs">
          <Text fw={700} c="orange.4">
            Админка
          </Text>
        </Group>
        <Stack gap={2} mt="sm">
          {adminNav.map((n) => (
            <RouterNavLink
              key={n.to}
              to={n.to}
              end={n.to === '/admin'}
              style={{ textDecoration: 'none' }}
            >
              {({ isActive }) => (
                <Box
                  px="sm"
                  py={8}
                  style={{
                    borderRadius: 8,
                    background: isActive
                      ? 'var(--mantine-color-orange-6)'
                      : 'transparent',
                  }}
                >
                  <Text size="sm" c={isActive ? '#fff' : 'gray.4'}>
                    {n.label}
                  </Text>
                </Box>
              )}
            </RouterNavLink>
          ))}
        </Stack>
        <Anchor href="/" c="dimmed" size="sm" mt="md" px="sm">
          ← Сайт
        </Anchor>
      </AppShell.Navbar>
      <AppShell.Main bg="var(--mantine-color-gray-0)">
        <Outlet />
      </AppShell.Main>
    </AppShell>
  )
}
