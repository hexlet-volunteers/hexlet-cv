import { http, delay } from 'msw'
import { inertiaJson } from '@mocks/inertia'
import { menu, activityCards } from '@mocks/account/index'

export const programsHandlers = [
  http.get('/account/programs', async ({ request }) => {
    console.log('MSW: handler hit', request.url)

    await delay()

    return inertiaJson({
      component: 'Account/Programs/Index',
      props: {
        menu,
        activityCards,
        programs: [
          {
            id: 1,
            title: 'Профессия Аналитик данных',
            progress: 46,
            skills: ['SQL', 'Python', 'BI'],
          },
          {
            id: 2,
            title: 'Data Science Starter',
            progress: 12,
            skills: ['Python', 'NumPy', 'Pandas'],
          },
        ],
      },
      url: '/account/programs',
    })
  }),
]
