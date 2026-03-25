import { AdminInterviews, type TProps } from "@widgets/admin-interviews"
import { AdminLayout } from "../components/AdminLayout"

const Interview = ({ interviews }: TProps) => {
    return <AdminInterviews interviews={interviews}/>
}

Interview.layout = (page: React.ReactNode) => <AdminLayout>{page}</AdminLayout>

export default Interview