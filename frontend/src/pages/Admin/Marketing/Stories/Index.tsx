import { withMarketingLayout } from '../components/withMarketingLayout'
import {
  MarketingStories,
  type MarketingStoriesDTO,
} from '@widgets/admin-marketing'

/**
 * Пропсы для страницы управления историями.
 */
export interface PageProps {
  /** Массив историй для отображения на странице. */
  stories: MarketingStoriesDTO[]
}

/**
 * Компонент страницы управления историями (stories) в админ-панели.
 * Отображает список историй с использованием компонента MarketingStories.
 *
 * @param props - Пропсы компонента.
 * @param props.stories - Массив историй для отображения.
 * @returns React-компонент страницы историй.
 */
const Stories = (props: PageProps) => {
  const { stories } = props
  return <MarketingStories stories={stories} />
}

/**
 * Компонент страницы историй, обернутый в макет маркетинга.
 * Использует HOC withMarketingLayout для применения макетов.
 */
const StoriesWithLayout = withMarketingLayout<PageProps>(Stories)

export default StoriesWithLayout
