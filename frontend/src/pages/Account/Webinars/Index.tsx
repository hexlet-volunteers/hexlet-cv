import type { InertiaPage } from '@shared/types/inertia'
import { AppLayout } from '../components/AppLayout'

const Webinars: InertiaPage = () => {
  return 'Webinars'
}

Webinars.layout = (page) => <AppLayout>{page}</AppLayout>

export default Webinars
