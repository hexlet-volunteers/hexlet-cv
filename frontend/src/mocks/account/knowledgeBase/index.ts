import { defineGet } from '@mocks/msw/define'
import { menu as accountMenu, activityCards } from '@mocks/account'
import type { MswCtx } from '@mocks/msw/createCtx'

export const mockKnowledgeBase = [
  {
    id: 1,
    title: 'База знаний: SQL-шпаргалка',
    duration: '5 мин',
    type: 'Статья',
    url: ''
  },
  {
    id: 2,
    title: 'Интервью: Senior DA в ритейле',
    duration: '12 мин',
    type: 'Видео',
    url: ''
  },
  {
    id: 3,
    title: 'Название статьи 1',
    duration: '5 мин',
    type: 'Статья',
    url: ''
  },
  {
    id: 4,
    title: 'Название видео 2',
    duration: '12 мин',
    type: 'Видео',
    url: ''
  },
  {
    id: 5,
    title: 'Название статьи 3',
    duration: '5 мин',
    type: 'Статья',
    url: ''
  },
  {
    id: 6,
    title: 'Название видео 4',
    duration: '12 мин',
    type: 'Видео',
    url: ''
  },
  {
    id: 7,
    title: 'Название статьи 5',
    duration: '5 мин',
    type: 'Статья',
    url: ''
  },
  {
    id: 8,
    title: 'Название видео 6',
    duration: '12 мин',
    type: 'Видео',
    url: ''
  },
  {
    id: 9,
    title: 'Название статьи 7',
    duration: '5 мин',
    type: 'Статья',
    url: ''
  },
  {
    id: 10,
    title: 'Название видео 8',
    duration: '12 мин',
    type: 'Видео',
    url: ''
  },
  {
    id: 11,
    title: 'Название статьи 9',
    duration: '5 мин',
    type: 'Статья',
    url: ''
  },
  {
    id: 12,
    title: 'Название видео 10',
    duration: '12 мин',
    type: 'Видео',
    url: ''
  },
  {
    id: 13,
    title: 'Название статьи 11',
    duration: '5 мин',
    type: 'Статья',
    url: ''
  },
  {
    id: 14,
    title: 'Название видео 12',
    duration: '12 мин',
    type: 'Видео',
    url: ''
  },
  {
    id: 15,
    title: 'Название статьи 13',
    duration: '5 мин',
    type: 'Статья',
    url: ''
  },
  {
    id: 16,
    title: 'Название видео 14',
    duration: '12 мин',
    type: 'Видео',
    url: ''
  },
]

const baseProps = (ctx: MswCtx) => ({
  flash: {},
  errors: {},
  auth: { user: ctx.user },
  menu: accountMenu,
  activityCards,
})

export const knowledgeBaseHandlers = [
  defineGet('*/account/knowledge', ctx =>
    ctx.inertiaPage(
      'Account/Knowledge/Index',
      {
        ...baseProps(ctx),
        knowledgebase: mockKnowledgeBase,
      },
      200,
      `/${ctx.locale}/account/knowledge`
    )
  ),
]
