import { StudyPrograms, type TProps } from '@widgets/admin-study-programs'
import { AdminLayout } from '../components/AdminLayout'

/**
 * Отображает административную страницу со списком учебных программ.
 */
const Programs = ({ programs }: TProps) => {
  return <StudyPrograms programs={programs} />
}

Programs.layout = (page: React.ReactNode) => <AdminLayout>{page}</AdminLayout>

export default Programs
