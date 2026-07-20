import { NavLink as RouterNavLink } from 'react-router'
import {
  Box,
  Stack,
  Text,
  Badge,
  Group,
  Avatar,
  ScrollArea,
  Divider,
} from '@mantine/core'

interface NavItem {
  label: string
  to: string
  badge?: string
  badgeColor?: string
}

interface NavSection {
  title?: string
  items: NavItem[]
}

const sections: NavSection[] = [
  {
    items: [
      { label: 'Обзор', to: '/app' },
      {
        label: 'Онбординг',
        to: '/app/onboarding',
        badge: 'старт',
        badgeColor: 'green',
      },
    ],
  },
  {
    title: 'Поиск',
    items: [
      {
        label: 'Вакансии',
        to: '/app/vacancies',
        badge: '12',
        badgeColor: 'blue',
      },
      { label: 'Отклики', to: '/app/tracker', badge: '9', badgeColor: 'gray' },
      { label: 'Зарплаты', to: '/app/salaries' },
      { label: 'Рефералы', to: '/app/referrals' },
      { label: 'Аналитика', to: '/app/analytics' },
    ],
  },
  {
    title: 'Документы',
    items: [
      { label: 'Резюме', to: '/app/resume', badge: '78', badgeColor: 'gray' },
      { label: 'Письма', to: '/app/letters' },
      {
        label: 'Профиль',
        to: '/app/profile',
        badge: 'public',
        badgeColor: 'dark',
      },
    ],
  },
  {
    title: 'Подготовка',
    items: [
      {
        label: 'AI-собес',
        to: '/app/ai-interview',
        badge: 'AI',
        badgeColor: 'violet',
      },
      { label: 'Живые моки', to: '/app/mocks' },
      { label: 'База вопросов', to: '/app/questions' },
      { label: 'Записи собесов', to: '/app/recordings' },
    ],
  },
  {
    title: 'Опыт',
    items: [
      {
        label: 'Комм. проекты',
        to: '/app/commercial-projects',
        badge: 'набор',
        badgeColor: 'orange',
      },
    ],
  },
]

function SidebarLink({ item }: { item: NavItem }) {
  return (
    <RouterNavLink
      to={item.to}
      end={item.to === '/app'}
      style={{ textDecoration: 'none' }}
    >
      {({ isActive }) => (
        <Group
          justify="space-between"
          px="sm"
          py={8}
          style={{
            borderRadius: 8,
            background: isActive
              ? 'var(--mantine-color-blue-6)'
              : 'transparent',
            color: isActive ? '#fff' : 'var(--mantine-color-gray-4)',
          }}
        >
          <Text size="sm" fw={isActive ? 600 : 400}>
            {item.label}
          </Text>
          {item.badge && (
            <Badge
              size="xs"
              variant={item.badgeColor === 'dark' ? 'default' : 'filled'}
              color={item.badgeColor}
            >
              {item.badge}
            </Badge>
          )}
        </Group>
      )}
    </RouterNavLink>
  )
}

export function Sidebar() {
  return (
    <Box
      style={{ background: '#17171E', height: '100%', color: '#fff' }}
      p="sm"
    >
      <Group gap={8} px="sm" py="xs">
        <Text ff="monospace" fw={700} c="blue.4">
          &gt;_
        </Text>
        <Text fw={700}>Хекслет Карьера</Text>
      </Group>
      <ScrollArea h="calc(100vh - 150px)" mt="sm">
        <Stack gap="lg">
          {sections.map((section, i) => (
            <Stack key={i} gap={2}>
              {section.title && (
                <Text size="xs" tt="uppercase" c="dimmed" px="sm" mb={4}>
                  {section.title}
                </Text>
              )}
              {section.items.map((item) => (
                <SidebarLink key={item.to} item={item} />
              ))}
            </Stack>
          ))}
          <Box px="sm">
            <Box
              p="sm"
              style={{ border: '1px solid #2a2a35', borderRadius: 8 }}
            >
              <Group gap={6}>
                <Text size="sm" fw={600}>
                  Английский трек
                </Text>
                <Badge size="xs" color="blue">
                  скоро
                </Badge>
              </Group>
              <Text size="xs" c="dimmed" mt={4}>
                Резюме и mock-интервью на английском для международного рынка
              </Text>
            </Box>
          </Box>
        </Stack>
      </ScrollArea>
      <Divider my="sm" color="#2a2a35" />
      <Group px="sm" gap="sm" wrap="nowrap">
        <Avatar color="violet" radius="xl">
          АК
        </Avatar>
        <Box>
          <Text size="sm" fw={600}>
            Артём Крылов
          </Text>
          <Text size="xs" c="dimmed">
            Фронтенд · выпускник
          </Text>
        </Box>
      </Group>
    </Box>
  )
}
