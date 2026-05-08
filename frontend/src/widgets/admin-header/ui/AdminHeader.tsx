import { Group, Text } from '@mantine/core'
import { useTranslation } from 'react-i18next'

import classes from './AdminHeader.module.css'

type AdminHeaderProps = {
  renderLogin: () => JSX.Element
}

export const AdminHeader = ({ renderLogin }: AdminHeaderProps) => {
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
