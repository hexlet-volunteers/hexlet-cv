import { http, delay } from 'msw'
import { inertiaJson } from '@mocks/inertia'
import { accountMenu, activityCards } from '@mocks/account'

export const accountPurchases = {
  content: [
    {
      id: 1,
      date: '16.01.2025',
      name: 'Подписка 2026',
      price: 1200,
      status: 'active', // canceled, expired
      recieptUrl: '#', // url для скачивания чека или null если курс бесплатный
    },
    {
      id: 2,
      date: '17.01.2025',
      name: 'Вебинар 2026',
      price: 112,
      status: 'active', // canceled, expired
      recieptUrl: '#',
    },
    {
      id: 3,
      date: '17.01.2025',
      name: 'Курс JS',
      price: 0,
      status: 'active', // canceled, expired
    },
  ],
  totalItems: 3,
}

export const purchaseHandlers = [
  http.get('/account/purchase', async () => {
    await delay()
    return inertiaJson({
      component: 'Account/Purchase/Index',
      props: {
        menu: accountMenu,
        activityCards,
        purchases: accountPurchases,
      },
      url: '/account/purchase',
    })
  }),
]
