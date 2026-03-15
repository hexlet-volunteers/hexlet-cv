import {
  Container,
  Title,
  SimpleGrid,
  Card,
  Text,
  Button,
  alpha,
} from '@mantine/core'
import { Link } from '@inertiajs/react'
import { useTranslation } from 'react-i18next'
import { useMantineTheme } from '@mantine/core'

export const KnowledgeBaseAndInterviews: React.FC = (): JSX.Element => {
  const { t } = useTranslation()
  const theme = useMantineTheme()

  // Карточки для демонстрации
  const cards = [
    {
      title: t('homePage.knowledgeBaseAndInterview.knowledgeBaseTitle'),
      description: t(
        'homePage.knowledgeBaseAndInterview.knowledgeBaseDescription',
      ),
      href: '',
    },
    {
      title: t('homePage.knowledgeBaseAndInterview.interviewQuestionsTitle'),
      description: t(
        'homePage.knowledgeBaseAndInterview.interviewQuestionsDescription',
      ),
      href: '',
    },
    {
      title: t('homePage.knowledgeBaseAndInterview.realInterviewVideosTitle'),
      description: t(
        'homePage.knowledgeBaseAndInterview.realInterviewVideosDescription',
      ),
      href: '',
    },
  ]

  return (
    <Container size="lg" py="xl">
      <Title order={1} mb="md">
        {t('homePage.knowledgeBaseAndInterview.title')}
      </Title>
      <SimpleGrid
        cols={{ base: 1, sm: 2, lg: 3 }}
        spacing="lg"
        verticalSpacing="lg"
      >
        {cards.map((card, index) => (
          <Card
            key={index}
            radius="lg"
            bg={alpha(theme.white, 0.08)}
            bd={`1px solid ${alpha(theme.white, 0.12)}`}
            component={Link}
            href={card.href}
          >
            <Text fw={600} fz="xl" mb="sm">
              {card.title}
            </Text>
            <Text fz="sm" mb="md">
              {card.description}
            </Text>
            <Button variant="white" color="black" radius="md" w="fit-content">
              {t('homePage.knowledgeBaseAndInterview.button')}
            </Button>
          </Card>
        ))}
      </SimpleGrid>
    </Container>
  )
}
