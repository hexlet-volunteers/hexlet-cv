import { http, delay } from 'msw'
import { inertiaJson } from '@mocks/inertia'
import { menu, activityCards } from '../index'

const interviews = [
  {
    id: 1,
    title: 'Как отвечать на вопросы про опыт',
    description: 'Разбор типовых вопросов о проектах, ролях и зонах ответственности на техническом интервью.',
    duration: '12 мин',
  },
  {
    id: 2,
    title: 'Frontend-интервью: JavaScript и асинхронность',
    description: 'Подборка вопросов по event loop, promises и сетевому взаимодействию с краткими пояснениями.',
    duration: '18 мин',
  },
  {
    id: 3,
    title: 'Интервью: SQL для аналитика',
    description: 'JOIN, оконные функции и типовые SQL-вопросы на собеседовании.',
    duration: '9 мин',
  },
]

export const interviewsHandlers = [
  http.get('/account/interviews', async ({ request }) => {
    await delay()

    const url = new URL(request.url)
    const page = parseInt(url.searchParams.get('page') || '0', 10)
    const pageSize = 9

    const start = page * pageSize
    const end = start + pageSize
    const pagedInterviews = interviews.slice(start, end)

    return inertiaJson({
      component: 'Account/Interview/Index',
      props: {
        menu,
        activityCards,
        interviews: pagedInterviews,
        pagination: {
          currentPage: page,
          totalPages: Math.ceil(interviews.length / pageSize),
          totalElements: interviews.length,
          pageSize,
        },
        activeMainSection: 'account',
        activeSubSection: 'interviews',
      },
      url: url.pathname + url.search,
    })
  }),
  http.get('/account/interviews/:id/read', async ({ params, request }) => {
    await delay()

    const interview = interviews.find(({ id }) => id === parseInt(params.id as string, 10))

    if (!interview) {
      return new Response(null, { status: 404 })
    }

    return inertiaJson({
      component: 'Account/Interview/Read',
      props: {
        menu,
        activityCards,
        interview,
        activeMainSection: 'account',
        activeSubSection: 'interviews',
      },
      url: new URL(request.url).pathname,
    })
  }),
]
