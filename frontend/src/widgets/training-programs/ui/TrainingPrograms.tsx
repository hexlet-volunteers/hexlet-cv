import { Container, Card, Text, Title, Grid, Button } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import type { TrainingCardDto } from '../types'

type TProps = {
  trainingPrograms: TrainingCardDto[]
}

export const TrainingPrograms: React.FC<TProps> = (props) => {
  const { trainingPrograms } = props
  const { t } = useTranslation()

  const renderItem = ({ title, description }: TrainingCardDto, index: number) => {
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
            {t('homePage.trainingPrograms.button')}
          </Button>
        </Card>
      </Grid.Col>
    )
  }

  return (
    <Container size="lg" py="xs">
      <Title order={1} fw="bold" mb="md">
        {t('homePage.trainingPrograms.title')}
      </Title>
      <Grid>
        {trainingPrograms.map(renderItem)}
      </Grid>
    </Container>
  )
}
