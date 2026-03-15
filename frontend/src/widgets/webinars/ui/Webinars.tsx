import { Container, Card, Text, Title, Grid, Button } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import type { WebinarsCardDto } from '../types'

export const Webinars = () => {
  const { t } = useTranslation()

  const webinars: WebinarsCardDto[] = [
    {
      description: 'Как рекрутеры читают резюме и что важно.',
      title: 'С HR о резюме',
    },
    {
      description: 'Как писать сопроводительные под JD.',
      title: 'Отклики, которые читают',
    },
    {
      description: 'Как готовиться к задачам и что проверяет.',
      title: 'Тестовые и live-coding',
    },
  ]

  const renderItem = (
    { description, title }: WebinarsCardDto,
    index: number,
  ) => {
    return (
      <Grid.Col
        key={index}
        span={{
          base: 12,
          md: 4,
        }}
      >
        <Card radius="lg" h="100%">
          <Title order={3}>{title}</Title>
          <Text size="sm" mb="lg" mt="xs">
            {description}
          </Text>
          <Button radius="lg" w="fit-content">
            {t('homePage.webinars.viewSchedule')}
          </Button>
        </Card>
      </Grid.Col>
    )
  }

  return (
    <Container size="lg" py="xs">
      <Title order={1} fw="bold" mb="md">
        {t('homePage.webinars.title')}
      </Title>
      <Grid>{webinars.map(renderItem)}</Grid>
    </Container>
  )
}
