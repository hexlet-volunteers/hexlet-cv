import type { TMenuItem } from '@shared/types/inertiaSharedData'
import { purchaseHandlers } from '@mocks/account/purchase'
import { progressHandlers, lessonsHandlers } from '@mocks/account/progress'
import { notificationsHandlers } from '@mocks/account/notifications/index'
import { defineGet } from '@mocks/msw/define'
import type { MswCtx } from '@mocks/msw/createCtx'
import { programsHandlers } from '@mocks/account/programs/index'

export const menu: TMenuItem[] = [
  { label: 'Мое обучение', link: '/account/my-progress' },
  { label: 'Покупки и подписки', link: '/account/purchase' },
  { label: 'Вебинары', link: '/account/webinars' },
  { label: 'База знаний' },
  { label: 'Интервью' },
  { label: 'Грейдирование' },
  { label: 'Программы обучения', link: '/account/programs' },
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

const baseProps = (ctx: MswCtx) => ({
  flash: {},
  errors: {},
  auth: { user: ctx.user },
  menu,
  activityCards,
})

const makeHandler = ({ component, url }: { component: string; url: string }) =>
  defineGet(`*${url}`, (ctx) =>
    ctx.inertiaPage(component, baseProps(ctx), 200, `/${ctx.locale}${url}`),
  )

const routes = [
  {
    component: 'Account/Purchase/Index',
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
  ...programsHandlers,
  ...notificationsHandlers,
]
