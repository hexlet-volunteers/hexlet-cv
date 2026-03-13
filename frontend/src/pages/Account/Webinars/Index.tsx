import type { InertiaPage } from '@shared/types/inertia'
import type { IWebinarsResponse } from '@entities/webinar/'
import { Container } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { IconVideo, IconShoppingCart } from '@tabler/icons-react'
import { PageHeader } from '@widgets/page-header'
import { EntityGrid } from '@widgets/entity-grid'
import { AppLayout } from '../components/AppLayout'
import { AccountWebinarCard } from '@widgets/account-webinar-card'
import { OpenSheduleButton } from '@features/open-shedule'

const Webinars: InertiaPage<IWebinarsResponse> = ({ webinars, pagination }) => {
  const { t } = useTranslation()
  return (
    <Container fluid>
      <PageHeader
        icon={<IconVideo />}
        title={t('accountPage.webinars.title')}
        actionButton={
          <OpenSheduleButton variant="outline">
            {t('accountPage.webinars.shedule')}
          </OpenSheduleButton>
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
        renderItem={(webinar) => (
          <AccountWebinarCard key={webinar.id} webinar={webinar} />
        )}
      />
    </Container>
  )
}
Webinars.layout = (page) => <AppLayout>{page}</AppLayout>

export default Webinars
