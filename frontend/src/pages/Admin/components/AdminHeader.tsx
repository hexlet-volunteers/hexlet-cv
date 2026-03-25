import { Group, Text, Button } from '@mantine/core'
import { Link } from '@inertiajs/react'
import { IconOutbound } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import classes from './AdminHeader.module.css'

export const AdminHeader = () => {
  const { t } = useTranslation()

  return (
    <Group className={classes.group} justify='space-between'>
      <Text fw={700} size='lg'>
        {t('adminPage.header.title')}
      </Text>
      <Button 
        component={Link} 
        href='' 
        leftSection={<IconOutbound size={20}/>}
        variant='default'
        radius='xs'
      >
        {t('adminPage.header.logoutButton')}
      </Button>
    </Group>
  )
}
