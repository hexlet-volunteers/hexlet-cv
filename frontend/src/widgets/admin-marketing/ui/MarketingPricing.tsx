import React from 'react'
import { Table, NumberInput, Text, TextInput } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { MarketingTable } from './MarketingTable'

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

const getFinalPrice = (price: number, discountPercent: number) => price * (1 - discountPercent / 100)

export const MarketingPricing: React.FC<TProps> = (props) => {
  const { pricing } = props
  const { t } = useTranslation()

  return (
    <MarketingTable data={pricing}>
      <Table withTableBorder verticalSpacing="md">
        <Table.Thead>
          <Table.Tr>
            <Table.Th fz="md" py="xs" w="15%">{t('adminPage.marketing.pricing.name')}</Table.Th>
            <Table.Th fz="md" py="xs" w="15%">{t('adminPage.marketing.pricing.originalPrice')}</Table.Th>
            <Table.Th fz="md" py="xs" w="15%">{t('adminPage.marketing.pricing.discountPercent')}</Table.Th>
            <Table.Th fz="md" py="xs" w="15%">{t('adminPage.marketing.pricing.total')}</Table.Th>
            <Table.Th fz="md" py="xs" w="40%">{t('adminPage.marketing.pricing.description')}</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {pricing?.map(price => (
            <Table.Tr key={price.id}>
              <Table.Td>
                <Text>
                  {price.name}
                </Text>
              </Table.Td>
              <Table.Td>
                <NumberInput
                  value={price.originalPrice}
                  size="xs"
                  w="100%"
                  miw={80}
                  maw={150}
                  allowNegative={false}
                  allowLeadingZeros={false}
                />
              </Table.Td>
              <Table.Td>
                <NumberInput
                  value={price.discountPercent}
                  size="xs"
                  w="100%"
                  miw={80}
                  maw={150}
                  allowNegative={false}
                  clampBehavior="strict"
                  allowLeadingZeros={false}
                  decimalScale={0}
                  min={0}
                  max={100}
                />
              </Table.Td>
              <Table.Td>
                <Text>
                  {getFinalPrice(price.originalPrice, price.discountPercent)}
                </Text>
              </Table.Td>
              <Table.Td>
                <TextInput
                  value={price.description}
                  size="xs"
                  readOnly
                />
              </Table.Td>
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </MarketingTable>
  )
}
