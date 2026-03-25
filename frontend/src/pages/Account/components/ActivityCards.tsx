import { SimpleGrid } from '@mantine/core'
import { DashboardCard } from './DashboardCard/DashboardCard'
import {
  IconBook,
  IconSparkles,
  IconRosetteDiscountCheck,
  IconCalendarEventFilled,
} from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import { usePage } from '@inertiajs/react'

export const ActivityCards: React.FC = () => {
  const { props } = usePage()
  const data = props.activityCards
  const { t } = useTranslation()

  if (!data) return null

  return (
    <SimpleGrid cols={{ base: 1, xs: 2, lg: 4 }} spacing="sm">
      <DashboardCard
        label={t('activityCards.courses_in_process')}
        value={data.coursesCount}
        icon={IconBook}
      />
      <DashboardCard
        label={t('activityCards.week_progress')}
        value={data.progress}
        icon={IconSparkles}
        description={t('activityCards.done_lessons')}
      />
      <DashboardCard
        label={t('activityCards.last_result')}
        value={data.lastResult.result}
        icon={IconRosetteDiscountCheck}
        description={data.lastResult.courseName}
      />
      <DashboardCard
        label={t('activityCards.nearest_event')}
        value={`${data.nearestEvent.date.day} · ${data.nearestEvent.date.time}`}
        icon={IconCalendarEventFilled}
        description={data.nearestEvent.eventName}
      />
    </SimpleGrid>
  )
}
