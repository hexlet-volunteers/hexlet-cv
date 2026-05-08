import { AdminWebinars, type WebinarDTO } from '@widgets/admin-webinars'

type WebinarsPageProps = {
  webinars: WebinarDTO[]
}

const Webinars = ({ webinars }: WebinarsPageProps) => {
  return <AdminWebinars webinars={webinars} />
}

export default Webinars
