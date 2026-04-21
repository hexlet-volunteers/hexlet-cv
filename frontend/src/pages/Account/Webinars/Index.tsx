import type { InertiaPage } from '@shared/types/inertia'
import type { WebinarsResponseDTO } from '@entities/webinar/'
import { Container } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { IconVideo, IconShoppingCart } from '@tabler/icons-react'
import { PageHeader } from '@widgets/page-header'
import { EntityGrid } from '@widgets/entity-grid'
import { AppLayout } from '../components/AppLayout'
import { AccountWebinarCard } from '@widgets/account-webinar-card'
import { OpenScheduleButton } from '@features/open-schedule'

/**
 * Страница «Вебинары» в личном кабинете (Inertia).
 * Рендерит заголовок, кнопку к расписанию и список вебинаров с пагинацией.
 */
const Webinars: InertiaPage<WebinarsResponseDTO> = ({
  webinars,
  pagination,
}) => {
  const { t } = useTranslation()
  return (
    <Container fluid>
      <PageHeader
        icon={<IconVideo />}
        title={t('accountPage.webinars.title')}
        actionButton={
          <OpenScheduleButton variant="outline">
            {t('accountPage.webinars.shedule')}
          </OpenScheduleButton>
        }
      />
      <EntityGrid
        data={webinars}
        pagination={pagination}
        baseUrl="/account/webinars"
        emptyConfig={{
          title: t('accountPage.webinars.noWebinars'),
          icon: IconShoppingCart,
          buttonLink: '#',
          buttonLabel: t('buttonsLabels.goToCatalog'),
        }}
        // Рендер конкретного элемента
        renderItem={(webinar) => <AccountWebinarCard webinar={webinar} />}
      />
    </Container>
  )
}
Webinars.layout = (page) => <AppLayout>{page}</AppLayout>

export default Webinars
