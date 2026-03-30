import { defineGet } from '@mocks/msw/define'
import { menu, activityCards } from '@mocks/account/index'

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
  defineGet(
    '*/account/my-progress/program/:id/lessons',
    (ctx, request) => {
      // 1. Получаем ID программы из URL (params.id всегда строка)
      const id = ctx.pathNoLocale.split('/').filter(Boolean).at(3) ?? ''
      const programId = parseInt(id, 10)

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

      return ctx.inertiaPage(
        'Account/Learning/MyProgress/Lessons',
        {
          flash: {},
          errors: {},
          auth: { user: ctx.user },
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
        200
      )
    },
  ),
]
