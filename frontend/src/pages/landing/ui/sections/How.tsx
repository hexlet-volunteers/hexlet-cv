import {
  Container,
  SimpleGrid,
  Stack,
  Text,
  Title,
  ThemeIcon,
} from '@mantine/core'

const steps = [
  'Подключаете источники вакансий',
  'Получаете матч и адаптируете резюме',
  'Ведёте отклики в трекере',
  'Готовитесь к собесу и выходите на оффер',
]

export function How() {
  return (
    <Container size="lg" py={64} id="how">
      <Title order={2} ta="center" mb="xl">
        Как это работает
      </Title>
      <SimpleGrid cols={{ base: 1, sm: 4 }} spacing="lg">
        {steps.map((s, i) => (
          <Stack key={i} gap="xs">
            <ThemeIcon size="xl" radius="xl">
              {i + 1}
            </ThemeIcon>
            <Text>{s}</Text>
          </Stack>
        ))}
      </SimpleGrid>
    </Container>
  )
}
