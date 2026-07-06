import { Link } from 'react-router'
import { Container, Title, Text, Button, Group, Stack, SimpleGrid, Card, Badge, Box, ThemeIcon } from '@mantine/core'

const features = [
  { icon: '🎯', title: 'Вакансии с матчем под профиль', text: 'Парсер собирает с hh.ru, Хабр Карьеры, Getmatch, Telegram, фриланс- и мировых площадок.' },
  { icon: '📄', title: 'Резюме и письма, которые проходят фильтры', text: 'AI-ревью, подгонка под ATS, весь пакет писем — от сопроводительного до торга.' },
  { icon: '🗂️', title: 'Трекер, который помнит всё', text: 'Kanban по этапам, автосвязка с версией резюме и письмами, напоминания о тишине.' },
  { icon: '🤖', title: 'AI-собеседования и живые моки', text: 'Тренировка с прогоном кода реальными тестами и разбор отказов в план действий.' },
]
const steps = ['Подключаете источники вакансий', 'Получаете матч и адаптируете резюме', 'Ведёте отклики в трекере', 'Готовитесь к собесу и выходите на оффер']

export function Landing() {
  return (
    <Box>
      <Box bg="blue.6" c="white" ta="center" py={8}>
        <Text size="sm">Выпускникам Хекслета 2026 года тариф «Про» — бесплатно на 6 месяцев →</Text>
      </Box>
      <Container size="lg" py={64}>
        <Stack align="center" gap="lg" ta="center">
          <Title order={1} size={48} maw={800}>От отклика до оффера — в одном сервисе</Title>
          <Text size="xl" c="dimmed" maw={640}>
            Вакансии с трёх площадок, резюме с AI-ревью, письма, трекер откликов и тренировочные собеседования
          </Text>
          <Group>
            <Button size="lg" component={Link} to="/register">Начать бесплатно</Button>
            <Button size="lg" variant="default" component={Link} to="/app">Смотреть демо</Button>
          </Group>
          <Text size="sm" c="dimmed">Бесплатно для студентов Хекслета · без карты</Text>
        </Stack>
      </Container>

      <Box bg="gray.0" py={64} id="features">
        <Container size="lg">
          <Stack gap={4} mb="xl" ta="center">
            <Title order={2}>Всё, что между «ищу работу» и «вышел на работу»</Title>
            <Text c="dimmed">Один контекст на все шаги: вакансия знает про ваше резюме, письмо — про вакансию</Text>
          </Stack>
          <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="lg">
            {features.map((f) => (
              <Card key={f.title} withBorder radius="md" padding="lg">
                <ThemeIcon size="lg" radius="md" variant="light" mb="sm"><span>{f.icon}</span></ThemeIcon>
                <Text fw={600}>{f.title}</Text>
                <Text c="dimmed" size="sm" mt={4}>{f.text}</Text>
              </Card>
            ))}
          </SimpleGrid>
        </Container>
      </Box>

      <Container size="lg" py={64} id="how">
        <Title order={2} ta="center" mb="xl">Как это работает</Title>
        <SimpleGrid cols={{ base: 1, sm: 4 }} spacing="lg">
          {steps.map((s, i) => (
            <Stack key={i} gap="xs">
              <ThemeIcon size="xl" radius="xl">{i + 1}</ThemeIcon>
              <Text>{s}</Text>
            </Stack>
          ))}
        </SimpleGrid>
      </Container>

      <Box bg="gray.0" py={64} id="pricing">
        <Container size="sm" ta="center">
          <Title order={2}>Тарифы</Title>
          <Text c="dimmed" mt="xs">Бесплатно студентам Хекслета · Про — для остальных</Text>
          <Group justify="center" mt="lg">
            <Button component={Link} to="/register">Начать</Button>
            <Button variant="default" component={Link} to="/register">Попробовать 7 дней</Button>
          </Group>
        </Container>
      </Box>

      <Container size="sm" py={64} ta="center">
        <Badge mb="md">5 минут</Badge>
        <Title order={2}>Начните с резюме — это 5 минут</Title>
        <Button size="lg" mt="lg" component={Link} to="/register">Смотреть демо</Button>
      </Container>
    </Box>
  )
}
