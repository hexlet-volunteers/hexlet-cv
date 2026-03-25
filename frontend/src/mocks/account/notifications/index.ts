import { http, delay } from 'msw'
import { inertiaJson } from '@mocks/inertia'
import { menu, activityCards } from '../index'

export const notificationsHandlers = [
    http.get('/account/notifications', async ({ request }) => {
    console.log('MSW: handler hit', request.url)

    await delay()

        return inertiaJson({
            component: 'Account/Notifications/Index',
            props: {
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
            url: '/account/notifications',
        })
    }),
]
