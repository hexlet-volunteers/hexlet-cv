import { Table } from '@mantine/core'
import React from 'react'
import { useTranslation } from 'react-i18next'
import { MarketingTable } from './MarketingTable'
import { TableNumberInput, TableText, TableTextInput, TableTh } from '@shared/ui/Table'

export type MarketingPricingDTO = {
  id: number
  name: string
  originalPrice: number
  discountPercent: number
  finalPrice: number
  description: string
  discountAmount: number
  savings: number
  hasDiscount: boolean
  isFree: boolean
  createdAt?: string
  updatedAt?: string
}

export interface TProps {
  pricing: MarketingPricingDTO[]
}

export const MarketingPricing: React.FC<TProps> = (props) => {
  const { pricing } = props
  const { t } = useTranslation()

  return (
    <MarketingTable data={pricing}>
      <Table withTableBorder verticalSpacing="md">
        <Table.Thead>
          <Table.Tr>
            <TableTh w="15%">{t('adminPage.marketing.pricing.name')}</TableTh>
            <TableTh w="15%">{t('adminPage.marketing.pricing.originalPrice')}</TableTh>
            <TableTh w="15%">{t('adminPage.marketing.pricing.discountPercent')}</TableTh>
            <TableTh w="15%">{t('adminPage.marketing.pricing.total')}</TableTh>
            <TableTh w="40%">{t('adminPage.marketing.pricing.description')}</TableTh>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {pricing?.map(price => (
            <Table.Tr key={price.id}>
              <TableText value={price.name} />
              <TableNumberInput
                value={price.originalPrice}
                size="xs"
                miw={80}
                maw={150}
              />
              <TableNumberInput
                value={price.discountPercent}
                size="xs"
                miw={80}
                maw={150}
                clampBehavior="strict"
                decimalScale={0}
                max={100}
              />
              <TableText value={price.originalPrice * (1 - price.discountPercent / 100)} />
              <TableTextInput
                value={price.description}
                readOnly
                size="xs"
              />
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </MarketingTable>
  )
}
