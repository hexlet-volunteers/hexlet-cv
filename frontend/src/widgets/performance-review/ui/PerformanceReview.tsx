import { Container, Card, Text, Title, Grid, Button } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import type { PerformanceCardDto } from '../types'

type TProps = {
  performanceReview: PerformanceCardDto[]
}

export const PerformanceReview: React.FC<TProps> = (props) => {
  const { performanceReview } = props
  const { t } = useTranslation()

  const renderItem = ({ title, description }: PerformanceCardDto, index: number) => {
    return (
      <Grid.Col
        key={index}
        span={{
          base: 12,
          md: 4,
        }}
      >
        <Card radius="lg" h="100%">
          <Title order={3}>
            {title}
          </Title>
          <Text size="sm" mb="lg" mt="xs">
            {description}
          </Text>
          <Button radius="lg" w="fit-content">
            {t('homePage.performanceReview.button')}
          </Button>
        </Card>
      </Grid.Col>
    )
  }

  return (
    <Container size="lg" py="xs">
      <Title order={1} fw="bold" mb="md">
        {t('homePage.performanceReview.title')}
      </Title>
      <Grid>
        {performanceReview.map(renderItem)}
      </Grid>
    </Container>
  )
}
