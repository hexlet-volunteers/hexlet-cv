import { http, delay } from 'msw'
import { inertiaJson } from '@mocks/inertia'
import { type UserDTO } from '@entities/user'
import { mockMenu } from '@mocks/adminMenu'

const mockUsers: UserDTO[] = [
  {
    id: 1,
    name: 'Иванов Иван',
    email: 'ivanov@gmail.com',
    login: 'ivan87',
    role: 'СONSULTANT',
    isSubscribed: false,
    tarif: 'pro',
    startsAt: '2024-01-15T10:30:00Z',
    endsAt: '2025-01-15T10:30:00Z',
  },
  {
    id: 2,
    name: 'Кравцова Мария',
    email: 'maryk@list.ru',
    login: 'mary',
    role: 'ADMIN',
    isSubscribed: true,
    tarif: 'pro',
    startsAt: '2025-01-15T10:30:00Z',
    endsAt: '2026-05-15T10:30:00Z',
  },
  {
    id: 3,
    name: 'Иванова Анна',
    email: 'anna@gmail.ru',
    login: 'anna',
    role: 'CURATOR',
    isSubscribed: true,
    tarif: 'pro',
    startsAt: '2026-01-15T10:30:00Z',
    endsAt: '2026-08-15T10:30:00Z',
  },
  {
    id: 4,
    name: 'Семенов Александр',
    email: 'sanek@gmail.ru',
    login: 'sanchez',
    role: 'USER',
    isSubscribed: null,
    tarif: null,
  },
  {
    id: 5,
    name: 'Олег Тестовый',
    email: 'oleg@gmail.ru',
    login: 'oleg',
    role: 'MENTOR',
    isSubscribed: true,
    tarif: 'pro',
    startsAt: '2024-01-15T10:30:00Z',
    endsAt: '2027-01-15T10:30:00Z',
  },
]

export const usersHandlers = [
  http.get('*/admin/users', async ({ request }) => {
    console.log('MSW: handler hit', request.url)

    await delay()

    return inertiaJson({
      component: 'Admin/Users/Index',
      props: {
        users: mockUsers,
        adminMenu: mockMenu,
      },
      url: new URL(request.url).pathname,
      version: 'msw-dev',
    })
  }),
]
