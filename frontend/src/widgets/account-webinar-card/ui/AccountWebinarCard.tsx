import { Text, Group, Stack } from '@mantine/core'
import type { TWebinar } from '@entities/webinar/'
import { useTranslation } from 'react-i18next'
import { WebinarRegistrationButton } from '@features/webinar-registration/'
import { AddToCalendareButton } from '@features/add-to-calendare'
import { RemindButton } from '@features/remind-button'
import { DsCard } from '@shared/uikit/DsCard/DsCard'

export const AccountWebinarCard: React.FC<{ webinar: TWebinar }> = ({
  webinar,
}) => {
  const { t } = useTranslation()

  const actionButton = webinar.isPublic ? (
    <WebinarRegistrationButton variant="filled" webinarId={webinar.id}>
      {t('accountPage.webinars.registration')}
    </WebinarRegistrationButton>
  ) : (
    <AddToCalendareButton variant="filled" itemId={webinar.id}>
      {t('accountPage.webinars.addToCalendar')}
    </AddToCalendareButton>
  )

  const webinarLocation = webinar.isOnline
    ? t('accountPage.webinars.location.online')
    : t('accountPage.webinars.location.offline')

  return (
    <DsCard>
      <DsCard.Content>
        <Stack gap={5}>
          <Text fw="bold" size="lg" lineClamp={2}>
            {webinar.title}
          </Text>
          <Group gap="xs" mb="sm">
            <Text size="sm" c="dimmed" lineClamp={2}>
              {webinar.date}
            </Text>
            <Text size="sm" c="dimmed" lineClamp={2}>
              {webinar.time}
            </Text>
            <Text size="sm" c="dimmed" span>
              ·
            </Text>
            <Text size="sm" c="dimmed" lineClamp={2}>
              {webinarLocation}
            </Text>
          </Group>

          {/* card footer */}
          <Group justify="space-between" align="center">
            {actionButton}
            <RemindButton itemId={webinar.id} />
          </Group>
        </Stack>
      </DsCard.Content>
    </DsCard>
  )
}
