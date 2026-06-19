import { Stack, Text } from '@mantine/core'

// временный интерфейс
interface ILesson {
  id: number
  isCompleted: boolean
  startedAt: string
  completedAt: string
  timeSpentMinutes: number
  lessonId: number
  programProgressId: number
  userId: number
  lessonTitle: string
}

/**
 * Описывает пропсы временной страницы уроков.
 */
type LessonsPageProps = {
  /** Прогресс пользователя по урокам выбранной программы. */
  lessonsProgress: ILesson[]
}

/**
 * Отображает временную страницу со списком уроков выбранной программы.
 */
const LessonsPage = ({ lessonsProgress }: LessonsPageProps) => {
  // временная заглушка
  return (
    <Stack>
      {lessonsProgress.map((lesson) => (
        <Text key={lesson.id}>{lesson.lessonTitle}</Text>
      ))}
    </Stack>
  )
}

export default LessonsPage
