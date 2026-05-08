import { useTranslation } from 'react-i18next'

/**
 * Отображает корневую страницу административного раздела.
 */
const AdminPage = () => {
  const { t } = useTranslation()
  return <div>{t('adminPage.header.title')}</div>
}

export default AdminPage
