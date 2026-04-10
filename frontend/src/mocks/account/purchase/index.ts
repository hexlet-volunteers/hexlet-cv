import { defineGet } from '@mocks/msw/define'
import { menu as accountMenu, activityCards } from '@mocks/account'
import type { MswCtx } from '@mocks/msw/createCtx'

export const accountPurchases = {
  content: [
    {
      id: 1,
      date: '16.01.2025',
      name: 'Подписка 2026',
      price: 1200,
      status: 'active',
      recieptUrl: '#',
    },
    {
      id: 2,
      date: '17.01.2025',
      name: 'Вебинар 2026',
      price: 112,
      status: 'active',
      recieptUrl: '#',
    },
    {
      id: 3,
      date: '17.01.2025',
      name: 'Курс JS',
      price: 0,
      status: 'active',
    },
  ],
  totalItems: 3,
}

const baseProps = (ctx: MswCtx) => ({
  flash: {},
  errors: {},
  auth: { user: ctx.user },
  menu: accountMenu,
  activityCards,
  purchases: accountPurchases,
})

export const purchaseHandlers = [
  defineGet('*/account/purchase', ctx =>
    ctx.inertiaPage(
      'Account/Purchase/Index',
      baseProps(ctx),
      200,
      `/${ctx.locale}/account/purchase`
    )
  ),
]
