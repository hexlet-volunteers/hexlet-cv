import { AppLayout } from '@pages/Account/components/AppLayout'

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
const LessonsPage = ({ lessonsProgress }: { lessonsProgress: ILesson[] }) => {
  // временная заглушка
  return (
    <Stack>
      {lessonsProgress.map((lesson) => (
        <Text key={lesson.id}>{lesson.lessonTitle}</Text>
      ))}
    </Stack>
  )
}

// Оборачиваем страницу в ваш лайаут
LessonsPage.layout = (page: React.ReactNode) => <AppLayout>{page}</AppLayout>

export default LessonsPage
