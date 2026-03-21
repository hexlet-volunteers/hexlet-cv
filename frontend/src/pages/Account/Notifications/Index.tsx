import { AppLayout } from '../components/AppLayout'
import { PageHeader } from '@widgets/page-header'
import { Container, ScrollArea } from '@mantine/core'
import { IconBell } from '@tabler/icons-react'
import { NotificationsList } from '@widgets/notifications'
import { useTranslation } from 'react-i18next'
import type { InertiaPage } from '@shared/types/inertia'

/**
 * Тип DTO уведомления.
 */
export type NotificationDTO = {
    id: number
    title: string
    description: string
    createdAt: string
}

/**
 * Тип пропсов страницы "Уведомления".
 */
type TProps = {
  notifications?: NotificationDTO[]
}

/**
 * Страница "Уведомления" в личном кабинете пользователя.
 * Рендерит комопненты PageHeader и NotificationsList.
 * 
 * @param props - пропсы страницы, приходящие с бэкенда, в том числе список уведомлений { notifications: [...] }
 * @returns React-компонент
 */
const Notifications: InertiaPage<TProps> = ({ notifications }): JSX.Element => {
  const { t } = useTranslation()

  return (
    <Container fluid>
      <PageHeader
        icon={<IconBell />}
        title={t('accountPage.notifications.title')}
      />
      <ScrollArea h={600}>
        <NotificationsList notifications={notifications}/>
      </ScrollArea>
    </Container>
  )
}

Notifications.layout = (page: React.ReactNode) => <AppLayout>{page}</AppLayout>

export default Notifications
