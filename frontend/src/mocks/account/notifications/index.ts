import { menu, activityCards } from '@mocks/account'
import { defineGet } from '@mocks/msw/define'

export const notificationsHandlers = [
  defineGet(`*/account/notifications`, (ctx) =>
    ctx.inertiaPage(
      'Account/Notifications/Index',
      {
        menu,
        activityCards,
        notifications: [
          {
            id: 1,
            title: 'Домашняя работа проверена',
            description: 'Задача по группировкам SQL: 9/10.',
            createdAt: '2026-03-10T12:15:11.936Z',
          },
          {
            id: 2,
            title: 'Новый вебинар',
            description: 'HR-сессия добавлена в расписание.',
            createdAt: '2026-03-06T09:10:11.936Z',
          },
        ],
      },
      200,
      `/${ctx.locale}/account/notifications`,
    ),
  ),
]
