// import { http, delay } from 'msw'
// import { inertiaJson } from '@mocks/inertia'
// import { menu, activityCards } from '@mocks/account/index'

export const mockFavoritesList = [
  {
    id: 1,
    type: 'course',
    title: 'Курс по React',
    url: '/courses/react',
  },
  {
    id: 2,
    type: 'article',
    title: 'Статья о TypeScript',
    url: '/articles/typescript',
  },
  {
    id: 3,
    type: 'article',
    title: 'Типы TypeScript',
    url: '/articles/typescript',
  },
  {
    id: 4,
    type: 'article',
    title: 'Переменные TypeScript',
    url: '/articles/typescript',
  },
  {
    id: 5,
    type: 'article',
    title: 'Именование TypeScript',
    url: '/articles/typescript',
  },
  {
    id: 6,
    type: 'article',
    title: 'Массивы TypeScript',
    url: '/articles/typescript',
  },
  {
    id: 7,
    type: 'article',
    title: 'Объектные типы TypeScript',
    url: '/articles/typescript',
  },
]

// export const favoritesHandlers = [
//   http.get('/account/favorites', async ({ request }) => {
//     console.log('MSW: handler hit', request.url)

//     await delay()

//     return inertiaJson({
//       component: 'Account/Favorites/Index',
//       props: {
//         menu,
//         activityCards,
//         list: mockFavoritesList,
//       },
//       url: '/account/favorites',
//     })
//   }),
// ]
