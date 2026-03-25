import type { MenuItem } from '../inertiaSharedData/inertiaSharedProps'
import { ActivityCardsData } from '../inertiaSharedData/inertiaSharedProps'
import type { AdminMenuDTO } from '@pages/Admin/components/AdminNavbar'

declare module '@inertiajs/core' {
  interface PageProps {
    menu: MenuItem[]
    adminMenu: AdminMenuDTO[]
    activityCards: ActivityCardsData
  }
}
