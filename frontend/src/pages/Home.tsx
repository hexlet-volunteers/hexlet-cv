import { Container, getGradient, useMantineTheme, Stack } from '@mantine/core'
import { Footer } from '@widgets/Footer'
import { Header } from '@widgets/Header'
import { AboutUs } from '@widgets/about-us'
import { WhoWeAre } from '@widgets/who-we-are'
import { CommercialProjects } from '@widgets/commercial-projects'
import { MarketAnalytics } from '@widgets/market-analytics'
import {
  TrainingPrograms,
  type TrainingCardDto,
} from '@widgets/training-programs'
import { Communities } from '@widgets/communities'
import { KnowledgeBaseAndInterviews } from '@widgets/knowledge-base-and-interviews'
import { Webinars } from '@widgets/webinars'
import { TotaAi } from '@widgets/tota-ai'
import { Articles, type IArticle } from '@widgets/articles'
import {
  PerformanceReview,
  type PerformanceCardDto,
} from '@widgets/performance-review'
import { OurTeam, type OurTeamCardDto } from '@widgets/our-team'
import { AuthPanel } from '@widgets/auth-panel'

/**
 * Описывает данные, необходимые для рендера главной страницы.
 */
type IndexProps = {
  /** Список статей для блока материалов. */
  articles: IArticle[]
  /** Список карточек учебных программ. */
  trainingPrograms: TrainingCardDto[]
  /** Список карточек с результатами и отзывами. */
  performanceReview: PerformanceCardDto[]
  /** Список участников команды. */
  ourTeam: OurTeamCardDto[]
}

/**
 * Отображает главную страницу со всеми маркетинговыми секциями лендинга.
 */
const Index: React.FC<IndexProps> = (props) => {
  const { articles, trainingPrograms, performanceReview, ourTeam } = props

  const theme = useMantineTheme()

  return (
    <Stack
      // mih="100vh"
      bg={getGradient(
        {
          deg: 135,
          from: 'black',
          to: '#00031a',
        },
        theme,
      )}
      justify="space-between"
    >
      <Header renderLogin={AuthPanel} />
      {/* здесь передаем пропсы страниц для отрисовки, а пока БД пустая я сделал компонент пустышку для демонстрации */}
      <Container size="xl">
        <AboutUs />
        <WhoWeAre />
        <CommercialProjects />
        <MarketAnalytics />
        <TrainingPrograms trainingPrograms={trainingPrograms} />
        <PerformanceReview performanceReview={performanceReview} />
        <KnowledgeBaseAndInterviews />
        <Webinars />
        <TotaAi />
        <Articles articles={articles} />
        <OurTeam ourTeam={ourTeam} />
        <Communities />
      </Container>
      <Footer />
    </Stack>
  )
}

export default Index
