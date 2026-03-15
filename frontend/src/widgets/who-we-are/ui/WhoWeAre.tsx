import {
  Stack,
  Badge,
  Grid,
  Group,
  Container,
  Avatar,
  Text,
  Card,
} from '@mantine/core'
import { useTranslation } from 'react-i18next'

export const WhoWeAre: React.FC = () => {
  const { t } = useTranslation()

  const features = [
    t('homePage.whoWeAre.features.resume'),
    t('homePage.whoWeAre.features.apply'),
    t('homePage.whoWeAre.features.searchJobs'),
    t('homePage.whoWeAre.features.chatRecruiters'),
    t('homePage.whoWeAre.features.coverLetters'),
    t('homePage.whoWeAre.features.prepareInterviews'),
    t('homePage.whoWeAre.features.getExperience'),
  ]

  return (
    <Container size="lg" py="xs">
      <Card radius="lg" p="xl" bg="dark.6">
        <Grid>
          <Grid.Col
            span={{
              base: 12,
              md: 8,
            }}
          >
            <Stack gap={0}>
              <Text fz="h1" fw="bold">
                {t('homePage.whoWeAre.title')}
              </Text>
              <Text size="lg">{t('homePage.whoWeAre.subtitle')}</Text>
            </Stack>
            <Group mt="lg" gap="xs" wrap="wrap">
              {features.map((feature: string, index: number) => (
                <Badge key={index} color="blue" size="lg" tt="none">
                  <Text size="xs" fw="bold">
                    {feature}
                  </Text>
                </Badge>
              ))}
            </Group>
          </Grid.Col>
          <Grid.Col
            span={{
              base: 12,
              md: 4,
            }}
          >
            <Group justify="center" align="center">
              <Avatar src="" size={250} radius="50%" alt="Логотип Хекслет" />
            </Group>
          </Grid.Col>
        </Grid>
      </Card>
    </Container>
  )
}
