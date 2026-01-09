import {
  Container,
  getGradient,
  useMantineTheme,
  Stack
} from '@mantine/core'
import { Footer } from '@widgets/Footer'
import { Header } from '@widgets/Header'
import { AboutUs } from '@widgets/about-us'
import { WhoWeAre } from '@widgets/who-we-are'
import { CommercialProjects } from '@widgets/commercial-projects'
import { MarketAnalytics } from '@widgets/market-analytics'
import { TrainingPrograms, type TrainingCardDto } from '@widgets/training-programs'
import { Communities } from '@widgets/communities'
import { KnowledgeBaseAndInterviews } from '@widgets/knowledge-base-and-interviews'
import { Webinars } from '@widgets/webinars'
import { TotaAi } from '@widgets/tota-ai' 
import { Articles, type IArticle } from '@widgets/articles'
import { PerformanceReview, type PerformanceCardDto } from '@widgets/performance-review'
// import { Link } from '@inertiajs/react'

type PageSection = {
  id: number
  pageKey: string
  sectionKey: string
  title: string
  content: string
  active: boolean
  createdAt: string
  updatedAt: string
}

type IndexProps = {
  articles: IArticle[]
  trainingPrograms: TrainingCardDto[]
  performanceReview: PerformanceCardDto[]
  pageSections: Readonly<PageSection[]>
}

const Index: React.FC<IndexProps> = (props) => {
  const { articles, trainingPrograms, performanceReview, pageSections } = props

  const theme = useMantineTheme()
  console.log(`Page sections:`, pageSections)

  return (
    <Stack
      // mih="100vh"
      bg={getGradient({
        deg: 135,
        from: 'black',
        to: '#00031a',
      }, theme)}
      justify="space-between"
    >
      <Header />
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
        <Communities />
        {/* <Link href="/account">Personal Cabinet</Link> */}
      </Container>
      <Footer />
    </Stack>
  )
}

export default Index