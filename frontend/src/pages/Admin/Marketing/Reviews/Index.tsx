import { withMarketingLayout } from '../components/withMarketingLayout'
import { MarketingReviews, type MarketingReviewsDTO } from '@widgets/admin-marketing'

export interface PageProps {
  reviews: MarketingReviewsDTO[]
}

const Reviews = (props: PageProps) => {
  const { reviews } = props
  return <MarketingReviews reviews={reviews} />
}

const ReviewsWithLayout = withMarketingLayout(Reviews)

export default ReviewsWithLayout
