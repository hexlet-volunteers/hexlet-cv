import { Table, Paper, Button } from '@mantine/core'
import { IconShoppingCart, IconDownload } from '@tabler/icons-react'
import { usePage } from '@inertiajs/react'
import { useTranslation } from 'react-i18next'
import { EmptyPlaceholder } from '@shared/ui'
import type { IPurchasesResponse } from '@widgets/purchase-list/types'
import type { PageProps } from '@inertiajs/core'

export const PurchaseList: React.FC = () => {
  const { props } = usePage<PageProps & { purchases: IPurchasesResponse }>()
  const { t } = useTranslation()

  if (!props.purchases.content.length)
    return (
      <EmptyPlaceholder
        title={t('emptyPlaceholders.noPurchasesTitle')}
        icon={IconShoppingCart}
        buttonLink="https://hexlet.io/courses"
        buttonLabel={t('buttonsLabels.goToCatalog')}
      />
    )

  return (
    <Paper shadow="sm" radius="md" withBorder>
      <Table.ScrollContainer minWidth="sm" type="native">
        <Table>
          <Table.Thead>
            <Table.Tr>
              <Table.Th>
                {t('accountPage.purchases.table.order_number')}
              </Table.Th>
              <Table.Th>
                {t('accountPage.purchases.table.product_name')}
              </Table.Th>
              <Table.Th>
                {t('accountPage.purchases.table.purchase_date')}
              </Table.Th>
              <Table.Th>{t('accountPage.purchases.table.price')}</Table.Th>
              <Table.Th>{t('accountPage.purchases.table.status')}</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {props.purchases.content.map((item) => (
              <Table.Tr key={item.id}>
                <Table.Td>{item.id}</Table.Td>
                <Table.Td>{item.name}</Table.Td>
                <Table.Td>{item.date}</Table.Td>
                <Table.Td>{`${item.price}₽`}</Table.Td>
                <Table.Td>{item.status}</Table.Td>
                <Table.Td>
                  {item.recieptUrl && (
                    <Button
                      component="a"
                      download
                      href={item.recieptUrl}
                      size="compact-xs"
                      variant="subtle"
                      rightSection={<IconDownload size={14} />}
                    >
                      {t('accountPage.purchases.table.receipt')}
                    </Button>
                  )}
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      </Table.ScrollContainer>
    </Paper>
  )
}
