import {
  Box,
  Container,
  Group,
  Title,
  Text,
  Button,
  Badge,
} from '@mantine/core'
import { Link } from 'react-router'

export function Pricing() {
  return (
    <>
      <Box bg="gray.0" py={64} id="pricing">
        <Container size="sm" ta="center">
          <Title order={2}>Тарифы</Title>
          <Text c="dimmed" mt="xs">
            Бесплатно студентам Хекслета · Про — для остальных
          </Text>
          <Group justify="center" mt="lg">
            <Button component={Link} to="/register">
              Начать
            </Button>
            <Button variant="default" component={Link} to="/register">
              Попробовать 7 дней
            </Button>
          </Group>
        </Container>
      </Box>
      <Container size="sm" py={64} ta="center">
        <Badge mb="md">5 минут</Badge>
        <Title order={2}>Начните с резюме — это 5 минут</Title>
        <Button size="lg" mt="lg" component={Link} to="/register">
          Смотреть демо
        </Button>
      </Container>
    </>
  )
}
