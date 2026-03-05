import { AdminLayout } from '../components/AdminLayout'
import { MarketingLayout } from './components/MarketingLayout'

const MarketingPage = ({ children }: { children: React.ReactNode }) => {
  return (
    <MarketingLayout>
      {children}
    </MarketingLayout>
  )
}

MarketingPage.layout = (page: React.ReactNode) => <AdminLayout>{page}</AdminLayout>

export default MarketingPage
