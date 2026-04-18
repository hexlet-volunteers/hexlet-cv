import { Group, Text } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import classes from './AdminHeader.module.css'

/**
 * Описывает пропсы header-а административного раздела.
 */
interface AdminHeaderProps {
  /** Функция рендера блока авторизационных действий. */
  renderLogin: () => JSX.Element
}

/**
 * Отображает шапку административного раздела.
 */
export const AdminHeader = (props: AdminHeaderProps) => {
  const { renderLogin } = props

  const { t } = useTranslation()

  return (
    <Group className={classes.group} justify="space-between">
      <Text fw={700} size="lg">
        {t('adminPage.header.title')}
      </Text>
      {renderLogin()}
    </Group>
  )
}
