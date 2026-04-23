import { usePage } from '@inertiajs/react'
import { EmptyPlaceholder } from '@shared/ui'
import React from 'react'
import { useTranslation } from 'react-i18next'

/**
 * Пропсы для универсального компонента таблицы маркетинговых данных.
 *
 * @template T - Тип данных для элементов таблицы.
 */
export interface TProps<T> {
  /** Массив данных для отображения в таблице. Может быть null или undefined. */
  data: T[] | null | undefined
  /** Дочерние элементы для кастомного отображения таблицы. */
  children?: React.ReactNode
}

/**
 * Универсальный компонент-обертка для таблиц маркетинговых данных.
 * Обрабатывает состояния загрузки, отсутствия данных и ошибки.
 *
 * @template T - Тип данных для элементов таблицы.
 * @param props - Пропсы компонента.
 * @param props.data - Массив данных для отображения.
 * @param props.children - Дочерние элементы с кастомной таблицей.
 * @returns React-компонент с таблицей или заглушкой при отсутствии данных.
 */
export const MarketingTable = <T,>(props: TProps<T>) => {
  // eslint-disable-line @stylistic/comma-dangle
  const { data, children } = props
  const { t } = useTranslation()
  const { url } = usePage()

  if (!data) {
    return (
      <EmptyPlaceholder
        title={t('adminPage.marketing.loadError')}
        buttonLink={url}
        buttonLabel={t('adminPage.marketing.buttonReload')}
      />
    )
  }

  if (data.length === 0) {
    return <EmptyPlaceholder title={t('adminPage.marketing.EmptyData')} />
  }

  return <>{children}</>
}
