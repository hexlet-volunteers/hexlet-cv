import { defineGet } from '@mocks/msw/define'
import { menu as accountMenu, activityCards } from '@mocks/account'
import type { MswCtx } from '@mocks/msw/createCtx'

const mockFavoritesList = [
  {
    id: 1,
    type: 'course',
    title: 'Курс по React',
    url: '/account/programs',
  },
  {
    id: 2,
    type: 'article',
    title: 'Статья о TypeScript',
    url: '/account/programs',
  },
  {
    id: 3,
    type: 'article',
    title: 'Типы TypeScript',
    url: '/account/programs',
  },
  {
    id: 4,
    type: 'article',
    title: 'Переменные TypeScript',
    url: '/account/programs',
  },
  {
    id: 5,
    type: 'article',
    title: 'Именование TypeScript',
    url: '/account/programs',
  },
  {
    id: 6,
    type: 'article',
    title: 'Массивы TypeScript',
    url: '/account/programs',
  },
  {
    id: 7,
    type: 'article',
    title: 'Объектные типы TypeScript',
    url: '/account/programs',
  },
]

const baseProps = (ctx: MswCtx) => ({
  flash: {},
  errors: {},
  auth: { user: ctx.user },
  menu: accountMenu,
  activityCards,
})

export const favoritesHandlers = [
  defineGet('*/account/favorites', (ctx) =>
    ctx.inertiaPage(
      'Account/Favorites/Index',
      {
        ...baseProps(ctx),
        list: mockFavoritesList,
      },
      200,
      `/${ctx.locale}/account/favorites`,
    ),
  ),
]
