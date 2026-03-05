import { withMarketingLayout } from '../components/withMarketingLayout'
import { MarketingStories, type MarketingStoriesDTO } from '@widgets/admin-marketing'

export interface PageProps {
  stories: MarketingStoriesDTO[]
}

const Stories = (props: PageProps) => {
  const { stories } = props
  return <MarketingStories stories={stories} />
}

const StoriesWithLayout = withMarketingLayout(Stories)

export default StoriesWithLayout
