import { http, delay } from 'msw'
import { inertiaJson } from '@mocks/inertia'
import type { TMenuItem } from '@shared/types/inertiaSharedData'
import { purchaseHandlers } from './purchase'
import { progressHandlers, lessonsHandlers } from '@mocks/account/progress'
import { notificationsHandlers } from './notifications'

export const menu: TMenuItem[] = [
  { label: 'Мое обучение', link: '/account/my-progress' },
  { label: 'Покупки и подписки', link: '/account/purchase' },
  { label: 'Вебинары', link: '/account/webinars' },
  { label: 'База знаний' },
  { label: 'Интервью' },
  { label: 'Грейдирование' },
  { label: 'Программы обучения' },
  { label: 'Резюме' },
  { label: 'Сопроводительное' },
  { label: 'Автоотклики' },
  { label: 'Избранное' },
  { label: 'Уведомления', link: '/account/notifications' },
  { label: 'Поддержка' },
  { label: 'Настройки' },
]

export const activityCards = {
  coursesCount: 3, // количество курсов которые оформлены у пользователя
  progress: '3/10',
  lastResult: {
    courseName: 'Тест SQL',
    result: '85%',
  },
  nearestEvent: {
    eventName: 'Встреча с HR',
    date: {
      day: '20.01',
      time: '14:00',
    },
  },
}

const makeHandler = ({ component, url }: { component: string; url: string }) =>
  http.get(url, async ({ request }) => {
    console.log('MSW handler hit:', request.method, request.url)
    await delay()
    return inertiaJson({
      component,
      props: {
        errors: {},
        menu,
        activityCards,
      },
      url,
      version: 'msw-dev',
    })
  })

const routes = [
  {
    component: 'Account/Index',
    url: '/account',
  },
  {
    component: 'Account/Webinars/Index',
    url: '/account/webinars',
  },
] as const

export const handlers = [
  ...routes.map(makeHandler),
  ...purchaseHandlers,
  ...progressHandlers,
  ...lessonsHandlers,
  ...notificationsHandlers,
]
