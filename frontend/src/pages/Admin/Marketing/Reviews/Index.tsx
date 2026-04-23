import { withMarketingLayout } from '../components/withMarketingLayout'
import {
  MarketingReviews,
  type MarketingReviewsDTO,
} from '@widgets/admin-marketing'

/**
 * Пропсы для страницы управления отзывами.
 */
export interface PageProps {
  /** Массив отзывов для отображения на странице. */
  reviews: MarketingReviewsDTO[]
}

/**
 * Компонент страницы управления отзывами в админ-панели.
 * Отображает список отзывов с использованием компонента MarketingReviews.
 *
 * @param props - Пропсы компонента.
 * @param props.reviews - Массив отзывов для отображения.
 * @returns React-компонент страницы отзывов.
 */
const Reviews = (props: PageProps) => {
  const { reviews } = props
  return <MarketingReviews reviews={reviews} />
}

/**
 * Компонент страницы отзывов, обернутый в макет маркетинга.
 * Использует HOC withMarketingLayout для применения макетов.
 */
const ReviewsWithLayout = withMarketingLayout<PageProps>(Reviews)

export default ReviewsWithLayout
