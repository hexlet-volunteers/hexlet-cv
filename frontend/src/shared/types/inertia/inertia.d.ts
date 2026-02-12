import type { ActivityCardsData, AuthProps, MenuItem } from '../inertiaSharedData/inertiaSharedProps'
import type { AdminMenuDTO } from '@pages/Admin/components/AdminNavbar'

declare module '@inertiajs/core' {
  interface PageProps extends AuthProps {
    locale?: string
    menu: MenuItem[]
    adminMenu: AdminMenuDTO[]
    activityCards: ActivityCardsData
  }
}
