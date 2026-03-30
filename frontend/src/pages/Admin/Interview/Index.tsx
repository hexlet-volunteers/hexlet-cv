import { AdminInterviews, type TProps } from '@widgets/admin-interviews'
import { AdminLayout } from '../components/AdminLayout'

/**
 * Отображает административную страницу со списком интервью.
 */
const Interview = ({ interviews }: TProps) => {
  return <AdminInterviews interviews={interviews} />
}

Interview.layout = (page: React.ReactNode) => <AdminLayout>{page}</AdminLayout>

export default Interview
