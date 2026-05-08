import { StudyPrograms, type TProps } from '@widgets/admin-study-programs'

/**
 * Отображает административную страницу со списком учебных программ.
 */
const Programs = ({ programs }: TProps) => {
  return <StudyPrograms programs={programs} />
}

export default Programs
