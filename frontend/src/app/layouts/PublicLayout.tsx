import { Link, Outlet } from 'react-router'
import { Box, Group, Button, Text, Container, Anchor } from '@mantine/core'
import { Footer } from '@widgets/footer'

export function PublicLayout() {
  return (
    <Box>
      <Box
        style={{
          position: 'sticky',
          top: 0,
          zIndex: 100,
          backdropFilter: 'blur(8px)',
          background: 'rgba(255,255,255,0.85)',
          borderBottom: '1px solid var(--mantine-color-gray-2)',
        }}
      >
        <Container size="lg">
          <Group h={64} justify="space-between">
            <Link to="/" style={{ textDecoration: 'none', color: 'inherit' }}>
              <Group gap={8}>
                <Text ff="monospace" fw={700} c="blue.6">
                  &gt;_
                </Text>
                <Text fw={700}>Хекслет Карьера</Text>
              </Group>
            </Link>
            <Group gap="lg" visibleFrom="sm">
              <Anchor href="#features" c="dark">
                Возможности
              </Anchor>
              <Anchor href="#how" c="dark">
                Как это работает
              </Anchor>
              <Anchor href="#pricing" c="dark">
                Тарифы
              </Anchor>
              <Anchor component={Link} to="/blog" c="dark">
                Блог
              </Anchor>
            </Group>
            <Group gap="sm">
              <Button component={Link} to="/login" variant="subtle">
                Войти
              </Button>
              <Button component={Link} to="/register">
                Начать бесплатно
              </Button>
            </Group>
          </Group>
        </Container>
      </Box>
      <Outlet />
      <Footer />
    </Box>
  )
}
