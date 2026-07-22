import { AppShell, Burger, Group, Text } from '@mantine/core'
import { useDisclosure } from '@mantine/hooks'
import { Outlet } from 'react-router'
import { Sidebar } from '@widgets/sidebar'

export function AppLayout() {
  const [opened, { toggle }] = useDisclosure()
  return (
    <AppShell
      navbar={{ width: 264, breakpoint: 'sm', collapsed: { mobile: !opened } }}
      header={{ height: 56 }}
      padding="lg"
    >
      <AppShell.Header hiddenFrom="sm">
        <Group h="100%" px="md" gap="sm">
          <Burger opened={opened} onClick={toggle} size="sm" />
          <Text fw={700}>Хекслет Карьера</Text>
        </Group>
      </AppShell.Header>
      <AppShell.Navbar p={0} withBorder={false}>
        <Sidebar />
      </AppShell.Navbar>
      <AppShell.Main bg="var(--mantine-color-gray-0)">
        <Outlet />
      </AppShell.Main>
    </AppShell>
  )
}
