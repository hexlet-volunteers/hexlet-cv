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
import { TrainingPrograms } from '@widgets/training-programs'
import { Communities } from '@widgets/communities'
import { KnowledgeBaseAndInterviews } from '@widgets/knowledge-base-and-interviews'
import { Webinars } from '@widgets/webinars'
import { Articles, type IArticle } from '@widgets/articles'
import { Login } from '@widgets/login'

type IndexProps = {
  articles: IArticle[]
}

const Index: React.FC<IndexProps> = (props) => {
  const { articles } = props

  const theme = useMantineTheme()

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
      <Header renderLogin={Login} />
      {/* здесь передаем пропсы страниц для отрисовки, а пока БД пустая я сделал компонент пустышку для демонстрации */}
      <Container size="xl">
        <AboutUs />
        <WhoWeAre />
        <CommercialProjects />
        <MarketAnalytics />
        <TrainingPrograms />
        <KnowledgeBaseAndInterviews />
        <Webinars />
        <Articles articles={articles} />
        <Communities />
      </Container>
      <Footer />
    </Stack>
  )
}

export default Index
