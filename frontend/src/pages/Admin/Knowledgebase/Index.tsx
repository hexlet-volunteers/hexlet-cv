import { KnowledgeBase, type TProps } from '@widgets/knowledge-base'
import { AdminLayout } from '../components/AdminLayout'

/**
 * Отображает административную страницу со статьями базы знаний.
 */
const Knowledgebase = ({ articles }: TProps) => {
  return <KnowledgeBase articles={articles} />
}

Knowledgebase.layout = (page: React.ReactNode) => (
  <AdminLayout>{page}</AdminLayout>
)

export default Knowledgebase
