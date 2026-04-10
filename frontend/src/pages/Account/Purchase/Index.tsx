import type { InertiaPage } from '@shared/types/inertia'
import { AppLayout } from '../components/AppLayout'
import { PurchaseList } from '@widgets/purchase-list'
import { PageHeader } from '@widgets/page-header'
import { Container } from '@mantine/core'
import { IconShoppingCart } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import type { IPurchasesResponse } from '@widgets/purchase-list/types'

/**
 * Описывает пропсы страницы покупок.
 */
type PurchasePageProps = {
  /** Данные о покупках пользователя, полученные с сервера. */
  purchases: IPurchasesResponse
}

/**
 * Inertia-страница раздела покупок в аккаунте пользователя.
 *
 * Отрисовывает заголовок страницы и список покупок.
 */
const Purchase: InertiaPage<PurchasePageProps> = () => {
  const { t } = useTranslation()

  return (
    <Container fluid>
      <PageHeader
        icon={<IconShoppingCart />}
        title={t('accountPage.purchases.title')}
      />
      <PurchaseList />
    </Container>
  )
}

Purchase.layout = (page) => <AppLayout>{page}</AppLayout>

export default Purchase
