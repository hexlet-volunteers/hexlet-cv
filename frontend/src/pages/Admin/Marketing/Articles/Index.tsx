import { MarketingArticles, type MarketingArticlesDTO } from '@widgets/admin-marketing'
import { withMarketingLayout } from '../components/withMarketingLayout'

export interface PageProps {
  articles: MarketingArticlesDTO[]
}

const ArticlesPage = (props: PageProps) => {
  const { articles } = props
  return <MarketingArticles articles={articles} />
}

const ArticlesPageWithLayout = withMarketingLayout(ArticlesPage)

export default ArticlesPageWithLayout
