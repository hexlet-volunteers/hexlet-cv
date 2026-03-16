import { http, delay } from 'msw'
import { inertiaJson } from '@mocks/inertia'
import { menu, activityCards } from '@mocks/account/index'

export const coverLetterHandlers = [
  http.get('/account/coverletter', async ({ request }) => {
    console.log('MSW: handler hit', request.url)

    await delay()

    return inertiaJson({
      component: 'Account/CoverLetter/Index',
      props: {
        menu,
        activityCards,
        coverLetter: {
          header: 'Сопроводительное письмо',
          textLetter:
            'Здравствуйте! Я аналитик данных с опытом BI и продуктовой аналитики. Готов быстро включиться в задачи и доводить до измеримого результата.',
        },
      },
      url: '/account/coverletter',
    })
  }),
]
