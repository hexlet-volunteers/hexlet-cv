import { Stack, Paper, Text, Center, Loader } from '@mantine/core'
import { EmptyPlaceholder } from '@shared/ui'
import { IconBellX } from '@tabler/icons-react'
import { getDaysDiff, getRelativeDayI18nKey } from '@shared/lib'
import { useTranslation } from 'react-i18next'
import type { NotificationDTO } from '../types'

/**
 * Интерфейс пропсов компонента NotificationsList.
 * 
 * @property notifications - массив уведомлений пользователя.
 */
interface NotificationsListProps {
    notifications?: NotificationDTO[]
}

/**
 * Виджет для отображения уведомлений пользователя.
 * Рендерит список уведомлений с заголовком, описанием и временем создания.
 * Если данные не пришли - показывает лоадер, если данные равны undefined - показывает плейсхолдер пустого списка.
 * 
 * @param props - список уведомлений { notifications: [...] }
 * @returns React-компонент
 */
export const NotificationsList: React.FC<NotificationsListProps> = (props): JSX.Element => {
    const { notifications } = props
    const { t } = useTranslation()

    const relativeTimeText = {
        today: t('accountPage.notifications.today'),
        yesterday: t('accountPage.notifications.yesterday'),
        daysAgo: t('accountPage.notifications.daysAgo'),
    }

    const renderNotifications = () => {
        if (!notifications) {
            return (
                <Center h={200}>
                    <Loader color="blue" size="lg" />
                </Center>
            )
        }
        else if (!notifications.length) {
            return (
                <EmptyPlaceholder
                    title={t('accountPage.notifications.noNotifications')}
                    icon={IconBellX}
                />
            )
        }
        else {
            return (
                <Stack align='stretch' gap='sm'>
                    {notifications.map(not => {
                        const daysDifference = getDaysDiff(not.createdAt)
                        const i18key = getRelativeDayI18nKey(daysDifference)
                        return (
                            <Paper key={not.id} radius='lg' withBorder p='md'>
                                <Text size='lg'>{not.title}</Text>
                                <Text>{not.description}</Text>
                                <Text size='sm' pt='xs'>{relativeTimeText[i18key]}</Text>
                            </Paper>
                        )
                    })}
                </Stack>
            )
        }
    }


    return (
        <>
            {renderNotifications()}
        </>
    )
}