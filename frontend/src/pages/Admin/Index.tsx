import { AdminLayout } from './components/AdminLayout'
import { useTranslation } from 'react-i18next'

const AdminPage = () => {
  const { t } = useTranslation()
  return <div>{t('adminPage.header.title')}</div>
}

AdminPage.layout = (page: React.ReactNode) => <AdminLayout>{page}</AdminLayout>

export default AdminPage
