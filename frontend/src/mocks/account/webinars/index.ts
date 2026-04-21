import { http, delay } from 'msw'
import { inertiaJson } from '@mocks/inertia'
import { menu, activityCards } from '@mocks/account'

const webinars = [
  {
    id: 1,
    title: 'Название вебинара',
    date: '20.12.2024',
    time: '10:00',
    isOnline: true,
    isPublic: true,
  },
  {
    id: 2,
    title: 'Java Core',
    date: '21.12.2024',
    time: '15:00',
    isOnline: false,
    isPublic: false,
  },
]

export const webinarsHandlers = [
  http.get('/account/webinars', async ({ request }) => {
    await delay()

    // 1. Извлекаем номер страницы из URL (Inertia пришлет ?page=0, ?page=1 и т.д.)
    const url = new URL(request.url)
    const page = Number.parseInt(url.searchParams.get('page') || '0', 10)
    const pageSize = 9

    const start = page * pageSize
    const end = start + pageSize
    const pagedWebinars = webinars.slice(start, end)

    return inertiaJson({
      component: 'Account/Webinars/Index',
      props: {
        menu,
        activityCards,
        webinars: pagedWebinars,
        pagination: {
          currentPage: page, // Текущая страница из запроса
          totalPages: Math.ceil(webinars.length / pageSize),
          totalElements: webinars.length,
          pageSize: pageSize,
        },
        activeMainSection: 'account',
        activeSubSection: 'webinars',
      },
      url: '/account/webinars',
    })
  }),
]
