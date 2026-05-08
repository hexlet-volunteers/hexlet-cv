import { KnowledgeBase, type TProps } from '@widgets/knowledge-base'

/**
 * Отображает административную страницу со статьями базы знаний.
 */
const Knowledgebase = ({ articles }: TProps) => {
  return <KnowledgeBase articles={articles} />
}

export default Knowledgebase
