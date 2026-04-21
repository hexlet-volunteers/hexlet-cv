import { Text, Group, Stack } from '@mantine/core'
import type { WebinarDTO } from '@entities/webinar/'
import { useTranslation } from 'react-i18next'
import { RegisterWebinarButton } from '@features/register-webinar'
import { AddWebinarToCalendareButton } from '@features/add-webinar-to-calendare'
import { RemindWebinarButton } from '@features/remind-webinar'
import { DsCard } from '@shared/uikit/DsCard/DsCard'

export const AccountWebinarCard: React.FC<{ webinar: WebinarDTO }> = ({
  webinar,
}) => {
  const { t } = useTranslation()

  const actionButton = webinar.isPublic ? (
    <RegisterWebinarButton variant="filled" webinarId={webinar.id}>
      {t('accountPage.webinars.registration')}
    </RegisterWebinarButton>
  ) : (
    <AddWebinarToCalendareButton variant="filled" itemId={webinar.id}>
      {t('accountPage.webinars.addToCalendar')}
    </AddWebinarToCalendareButton>
  )

  const webinarLocationLabel = webinar.isOnline
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
              {webinarLocationLabel}
            </Text>
          </Group>

          {/* card footer */}
          <Group justify="space-between" align="center">
            {actionButton}
            <RemindWebinarButton itemId={webinar.id} />
          </Group>
        </Stack>
      </DsCard.Content>
    </DsCard>
  )
}
