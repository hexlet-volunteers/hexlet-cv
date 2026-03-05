import { withMarketingLayout } from '../components/withMarketingLayout'
import { MarketingPricing, type MarketingPricingDTO } from '@widgets/admin-marketing'

export interface PageProps {
  pricing: MarketingPricingDTO[]
}

const Pricing = (props: PageProps) => {
  const { pricing } = props
  return <MarketingPricing pricing={pricing} />
}

const PricingWithLayout = withMarketingLayout(Pricing)

export default PricingWithLayout
