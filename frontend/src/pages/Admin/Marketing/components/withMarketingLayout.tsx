import React from 'react'
import { MarketingLayout } from './MarketingLayout'
import { AdminLayout } from '../../components/AdminLayout'
import type { MarketingArticlesDTO, MarketingReviewsDTO, MarketingStoriesDTO, MarketingTeamDTO, MarketingPricingDTO } from '@widgets/admin-marketing'

export interface PageProps {
  articles?: MarketingArticlesDTO[]
  stories?: MarketingStoriesDTO[]
  reviews?: MarketingReviewsDTO[]
  team?: MarketingTeamDTO[]
  pricing?: MarketingPricingDTO[]
}

export function withMarketingLayout<P extends PageProps>(WrappedComponent: React.ComponentType<P>) {
  const ComponentWithLayout = (props: P) => {
    return (
      <MarketingLayout>
        <WrappedComponent {...props} />
      </MarketingLayout>
    )
  }

  ComponentWithLayout.layout = (page: React.ReactNode) => <AdminLayout>{page}</AdminLayout>

  return ComponentWithLayout
}
