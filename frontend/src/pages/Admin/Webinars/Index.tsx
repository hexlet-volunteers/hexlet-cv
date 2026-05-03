import { AdminWebinars, type WebinarDTO } from "@widgets/admin-webinars"
import { AdminLayout } from "../components/AdminLayout"

type WebinarsPageProps = {
    webinars: WebinarDTO[]
}

const Webinars = ({ webinars }: WebinarsPageProps) => {
    return <AdminWebinars webinars={webinars}/>
}

Webinars.layout = (page: React.ReactNode) => <AdminLayout>{page}</AdminLayout>

export default Webinars