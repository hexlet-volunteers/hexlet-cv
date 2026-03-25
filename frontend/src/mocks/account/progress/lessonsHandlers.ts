import { http, delay } from 'msw'
import { inertiaJson } from '@mocks/inertia'
import { menu, activityCards } from '../index'

const lessons = [
  {
    id: 15,
    isCompleted: true,
    startedAt: '2024-01-18T09:15:00',
    completedAt: '2024-01-18T11:30:00',
    timeSpentMinutes: 135,
    lessonId: 7,
    programProgressId: 4,
    userId: 123,
    lessonTitle: 'Введение в коллекции Java',
  },
  {
    id: 16,
    isCompleted: true,
    startedAt: '2024-01-18T09:15:00',
    completedAt: '2024-01-18T11:30:00',
    timeSpentMinutes: 135,
    lessonId: 8,
    programProgressId: 4,
    userId: 123,
    lessonTitle: 'Введение в объекты Java',
  },
  {
    id: 17,
    isCompleted: true,
    startedAt: '2024-01-18T09:15:00',
    completedAt: '2024-01-18T11:30:00',
    timeSpentMinutes: 135,
    lessonId: 8,
    programProgressId: 2,
    userId: 123,
    lessonTitle: 'Первый урок второй программы',
  },
  {
    id: 18,
    isCompleted: true,
    startedAt: '2024-01-18T09:15:00',
    completedAt: '2024-01-18T11:30:00',
    timeSpentMinutes: 135,
    lessonId: 8,
    programProgressId: 2,
    userId: 123,
    lessonTitle: 'Второй урок второй программы',
  },
]

export const lessonsHandlers = [
  http.get(
    '/account/my-progress/program/:id/lessons',
    async ({ params, request }) => {
      // 1. Получаем ID программы из URL (params.id всегда строка)
      const { id } = params
      const programId = parseInt(id as string, 10)

      await delay()

      // Оставляем только те уроки, которые относятся к этой программе
      const programLessons = lessons.filter(lesson => lesson.programProgressId === programId
      )

      const url = new URL(request.url)
      const page = parseInt(url.searchParams.get('page') || '0', 10)
      const pageSize = 10

      // 3. ПАГИНАЦИЯ: Режем уже отфильтрованный список
      const start = page * pageSize
      const end = start + pageSize
      const pagedLessons = programLessons.slice(start, end)

      return inertiaJson({
        component: 'Account/Learning/MyProgress/Lessons',
        props: {
          menu,
          activityCards,
          lessonsProgress: pagedLessons,
          programProgressId: programId,
          pagination: {
            currentPage: page,
            totalPages: Math.ceil(programLessons.length / pageSize),
            totalElements: programLessons.length,
            pageSize: pageSize,
          },
          activeMainSection: 'account',
          activeSubSection: 'my-progress',
        },
        url: url.pathname + url.search,
      })
    },
  ),
]
