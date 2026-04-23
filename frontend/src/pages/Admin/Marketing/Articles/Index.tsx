import {
  MarketingArticles,
  type MarketingArticlesDTO,
} from '@widgets/admin-marketing'
import { withMarketingLayout } from '../components/withMarketingLayout'

/**
 * Пропсы для страницы управления статьями.
 */
export interface PageProps {
  /** Массив статей для отображения на странице. */
  articles: MarketingArticlesDTO[]
}

/**
 * Компонент страницы управления статьями в админ-панели.
 * Отображает список статей с использованием компонента MarketingArticles.
 *
 * @param props - Пропсы компонента.
 * @param props.articles - Массив статей для отображения.
 * @returns React-компонент страницы статей.
 */
const ArticlesPage = (props: PageProps) => {
  const { articles } = props
  return <MarketingArticles articles={articles} />
}

/**
 * Компонент страницы статей, обернутый в макет маркетинга.
 * Использует HOC withMarketingLayout для применения макетов.
 */
const ArticlesPageWithLayout = withMarketingLayout<PageProps>(ArticlesPage)

export default ArticlesPageWithLayout
