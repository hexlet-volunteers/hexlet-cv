import { Container, Text, Card, Grid, Button } from '@mantine/core'
import { useTranslation } from 'react-i18next'

export const AboutUs = () => {
  const { t } = useTranslation()

  return (
    <Container size="lg" my="lg" py="xs">
      <Grid>
        <Grid.Col
          span={{
            base: 12,
            md: 7,
          }}
        >
          <Container mr="lg" pl={0}>
            <Text fz="3.2rem" fw={700} lh={1}>
              {t('homePage.aboutUs.title')}
            </Text>
            <Text size="lg" my="md">
              {t('homePage.aboutUs.description')}
            </Text>
            <Button radius="md" w="fit-content">
              <Text size="md">{t('homePage.aboutUs.buttons.tryFree')}</Text>
            </Button>
            <Button
              color="white"
              radius="md"
              variant="transparent"
              w="fit-content"
            >
              <Text size="md">
                {t('homePage.aboutUs.buttons.startWithProjects')}
              </Text>
            </Button>
          </Container>
        </Grid.Col>
        <Grid.Col
          span={{
            base: 12,
            md: 5,
          }}
        >
          <Card radius="lg" bg="dark.4" h={300}>
            {/* https://mantine.dev/dates/calendar/  ??? */}
          </Card>
        </Grid.Col>
      </Grid>
    </Container>
  )
}
