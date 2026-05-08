import { AdminInterviews, type TProps } from '@widgets/admin-interviews'

/**
 * Отображает административную страницу со списком интервью.
 */
const Interview = ({ interviews }: TProps) => {
  return <AdminInterviews interviews={interviews} />
}

export default Interview
