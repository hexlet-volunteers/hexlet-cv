import { http, delay } from 'msw'
import { inertiaJson } from '@mocks/inertia'
import { menu, activityCards } from '../index'

const progress = [
  {
    id: 1,
    programTitle: 'Java Core',
    lastLessonTitle: 'Объектно-ориентированное программирование',
    completedLessons: 5,
    totalLessons: 12,
    progressPercentage: 42,
    isCompleted: false,
    startedAt: '2024-01-15T10:30:00',
    lastActivityAt: '2026-01-20T14:25:00',
  },
  {
    id: 2,
    programTitle: 'Spring Framework',
    lastLessonTitle: null,
    completedLessons: 20,
    totalLessons: 20,
    progressPercentage: 100,
    isCompleted: true,
    startedAt: '2024-02-01T09:00:00',
    lastActivityAt: '2026-02-04T09:00:00',
  },
  {
    id: 3,
    programTitle: 'TS курс',
    lastLessonTitle: null,
    completedLessons: 0,
    totalLessons: 20,
    progressPercentage: 0,
    isCompleted: false,
    startedAt: '2024-02-01T09:00:00',
    lastActivityAt: '2026-02-01T09:00:00',
  },
  {
    id: 4,
    programTitle: 'Spring Framework',
    lastLessonTitle: null,
    completedLessons: 0,
    totalLessons: 20,
    progressPercentage: 0,
    isCompleted: false,
    startedAt: '2024-02-01T09:00:00',
    lastActivityAt: '2026-02-05T09:00:00',
  },
  {
    id: 5,
    programTitle: 'Spring Framework',
    lastLessonTitle: null,
    completedLessons: 0,
    totalLessons: 20,
    progressPercentage: 0,
    isCompleted: false,
    startedAt: '2024-02-01T09:00:00',
    lastActivityAt: '2024-02-01T09:00:00',
  },
  {
    id: 6,
    programTitle: 'Spring Framework',
    lastLessonTitle: null,
    completedLessons: 0,
    totalLessons: 20,
    progressPercentage: 0,
    isCompleted: false,
    startedAt: '2024-02-01T09:00:00',
    lastActivityAt: '2024-02-01T09:00:00',
  },
  {
    id: 7,
    programTitle: 'Spring Framework',
    lastLessonTitle: null,
    completedLessons: 0,
    totalLessons: 20,
    progressPercentage: 0,
    isCompleted: false,
    startedAt: '2024-02-01T09:00:00',
    lastActivityAt: '2024-02-01T09:00:00',
  },
  {
    id: 8,
    programTitle: 'Spring Framework',
    lastLessonTitle: null,
    completedLessons: 0,
    totalLessons: 20,
    progressPercentage: 0,
    isCompleted: false,
    startedAt: '2024-02-01T09:00:00',
    lastActivityAt: '2024-02-01T09:00:00',
  },
  {
    id: 9,
    programTitle: 'Spring Framework',
    lastLessonTitle: null,
    completedLessons: 0,
    totalLessons: 20,
    progressPercentage: 0,
    isCompleted: false,
    startedAt: '2024-02-01T09:00:00',
    lastActivityAt: '2024-02-01T09:00:00',
  },
  {
    id: 10,
    programTitle: 'Spring Framework',
    lastLessonTitle: null,
    completedLessons: 0,
    totalLessons: 20,
    progressPercentage: 0,
    isCompleted: false,
    startedAt: '2024-02-01T09:00:00',
    lastActivityAt: '2024-02-01T09:00:00',
  },
  {
    id: 11,
    programTitle: 'Spring Framework',
    lastLessonTitle: null,
    completedLessons: 0,
    totalLessons: 20,
    progressPercentage: 0,
    isCompleted: false,
    startedAt: '2024-02-01T09:00:00',
    lastActivityAt: '2024-02-01T09:00:00',
  },
  {
    id: 12,
    programTitle: 'Spring Framework',
    lastLessonTitle: null,
    completedLessons: 0,
    totalLessons: 20,
    progressPercentage: 0,
    isCompleted: false,
    startedAt: '2024-02-01T09:00:00',
    lastActivityAt: '2024-02-01T09:00:00',
  },
  {
    id: 13,
    programTitle: 'Spring Framework',
    lastLessonTitle: null,
    completedLessons: 0,
    totalLessons: 20,
    progressPercentage: 0,
    isCompleted: false,
    startedAt: '2024-02-01T09:00:00',
    lastActivityAt: '2024-02-01T09:00:00',
  },
]

export const progressHandlers = [
  http.get('/account/my-progress', async ({ request }) => {
    await delay()

    // 1. Извлекаем номер страницы из URL (Inertia пришлет ?page=0, ?page=1 и т.д.)
    const url = new URL(request.url)
    const page = parseInt(url.searchParams.get('page') || '0', 9)
    const pageSize = 9

    const start = page * pageSize
    const end = start + pageSize
    const pagedProgress = progress.slice(start, end)

    return inertiaJson({
      component: 'Account/Learning/MyProgress/Index',
      props: {
        menu,
        activityCards,
        progress: pagedProgress,
        pagination: {
          currentPage: page, // Текущая страница из запроса
          totalPages: Math.ceil(progress.length / pageSize),
          totalElements: progress.length,
          pageSize: pageSize,
        },
        activeMainSection: 'account',
        activeSubSection: 'my-progress',
      },
      url: '/account/my-progress',
    })
  }),
]
