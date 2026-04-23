import { withMarketingLayout } from '../components/withMarketingLayout'
import {
  MarketingPricing,
  type MarketingPricingDTO,
} from '@widgets/admin-marketing'

/**
 * Пропсы для страницы управления ценообразованием.
 */
export interface PageProps {
  /** Массив элементов ценообразования для отображения на странице. */
  pricing: MarketingPricingDTO[]
}

/**
 * Компонент страницы управления ценообразованием в админ-панели.
 * Отображает список тарифов и цен с использованием компонента MarketingPricing.
 *
 * @param props - Пропсы компонента.
 * @param props.pricing - Массив элементов ценообразования для отображения.
 * @returns React-компонент страницы ценообразования.
 */
const Pricing = (props: PageProps) => {
  const { pricing } = props
  return <MarketingPricing pricing={pricing} />
}

/**
 * Компонент страницы ценообразования, обернутый в макет маркетинга.
 * Использует HOC withMarketingLayout для применения макетов.
 */
const PricingWithLayout = withMarketingLayout<PageProps>(Pricing)

export default PricingWithLayout
