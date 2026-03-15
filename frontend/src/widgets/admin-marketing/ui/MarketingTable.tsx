import { usePage } from '@inertiajs/react'
import { EmptyPlaceholder } from '@shared/ui'
import React from 'react'
import { useTranslation } from 'react-i18next'

export interface TProps<T> {
  data: T[] | null | undefined
  children?: React.ReactNode
}

export const MarketingTable = <T, >(props: TProps<T>) => { // eslint-disable-line @stylistic/comma-dangle
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
    return (
      <EmptyPlaceholder
        title={t('adminPage.marketing.EmptyData')}
      />
    )
  }

  return <>{children}</>
}
