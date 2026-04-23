import React from 'react'
import { Table, NumberInput, Text, TextInput } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { MarketingTable } from './MarketingTable'

/**
 * Тип данных для элемента ценообразования в маркетинговом разделе.
 */
export type MarketingPricingDTO = {
  /** Уникальный идентификатор элемента. */
  id: number
  /** Название тарифа или услуги. */
  name: string
  /** Исходная цена до скидки. */
  originalPrice: number
  /** Процент скидки. */
  discountPercent: number
  /** Финальная цена после применения скидки. */
  finalPrice: number
  /** Описание тарифа или услуги. */
  description: string
  /** Сумма скидки в денежном выражении. */
  discountAmount: number
  /** Экономия для клиента. */
  savings: number
  /** Есть ли действующая скидка. */
  hasDiscount: boolean
  /** Является ли тариф бесплатным. */
  isFree: boolean
  /** Дата создания (опционально). */
  createdAt?: string
  /** Дата последнего обновления (опционально). */
  updatedAt?: string
}

/**
 * Пропсы для компонента MarketingPricing.
 */
export interface TProps {
  /** Массив элементов ценообразования для отображения в таблице. */
  pricing: MarketingPricingDTO[]
}

/**
 * Компонент для отображения списка тарифов и цен в админ-панели.
 * Отображает название, исходную цену, процент скидки, финальную цену и описание.
 *
 * @param props - Пропсы компонента.
 * @param props.pricing - Массив элементов ценообразования для отображения.
 * @returns React-компонент с таблицей тарифов.
 */
export const MarketingPricing: React.FC<TProps> = (props) => {
  const { pricing } = props
  const { t } = useTranslation()

  return (
    <MarketingTable data={pricing}>
      <Table withTableBorder verticalSpacing="md">
        <Table.Thead>
          <Table.Tr>
            <Table.Th fz="md" py="xs" w="15%">
              {t('adminPage.marketing.pricing.name')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="15%">
              {t('adminPage.marketing.pricing.originalPrice')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="15%">
              {t('adminPage.marketing.pricing.discountPercent')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="15%">
              {t('adminPage.marketing.pricing.total')}
            </Table.Th>
            <Table.Th fz="md" py="xs" w="40%">
              {t('adminPage.marketing.pricing.description')}
            </Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {pricing?.map((price) => (
            <Table.Tr key={price.id + price.name}>
              <Table.Td>
                <Text>{price.name}</Text>
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
                <Text>{price.finalPrice}</Text>
              </Table.Td>
              <Table.Td>
                <TextInput value={price.description} size="xs" readOnly />
              </Table.Td>
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </MarketingTable>
  )
}
