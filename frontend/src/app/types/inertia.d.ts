import type { AccountMenuItemDTO } from '@entities/account-menu'
import type { AdminMenuDTO } from '@entities/admin-menu'
import type { ActivityCardDTO } from '@entities/activity-card'
import type { UserDTO } from '@entities/session'

declare module '@inertiajs/core' {
  interface PageProps {
    account: {
      activeMenuKey: string
      menu: AccountMenuItemDTO[]
    }
    activityCards: ActivityCardDTO
    adminMenu: AdminMenuDTO[]
    auth: {
      user: UserDTO
    }
    locale?: string
  }
}
