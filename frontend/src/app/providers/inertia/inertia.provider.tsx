import { createInertiaApp } from '@inertiajs/react'
import { createRoot } from 'react-dom/client'
import { UIProvider } from '@ui/ui.provider'
import I18nProvider from '@i18n/i18n.provider'
import { AccountLayout } from '../../layouts/account'
import { AdminLayout } from '../../layouts/admin'
import { StrictMode, type ReactNode } from 'react'
import type { Page } from '@inertiajs/core'
import type { InertiaPageModule } from '@shared/types/inertia'

const pages = import.meta.glob<InertiaPageModule>('../../../pages/**/*.tsx', {
  eager: true,
})

export type PageLayoutScope = 'account' | 'admin' | null

export const getPageLayoutScope = (name: string): PageLayoutScope => {
  if (name === 'Account' || name.startsWith('Account/')) {
    return 'account'
  }

  if (name === 'Admin' || name.startsWith('Admin/')) {
    return 'admin'
  }

  return null
}

const renderAccountLayout = (page: ReactNode) => (
  <AccountLayout>{page}</AccountLayout>
)

const renderAdminLayout = (page: ReactNode) => <AdminLayout>{page}</AdminLayout>

const getV2PageLayout = (name: string) => {
  const scope = getPageLayoutScope(name)

  if (scope === 'account') {
    return renderAccountLayout
  }

  if (scope === 'admin') {
    return renderAdminLayout
  }

  return undefined
}

export const initInertia = (page?: Page) => {
  createInertiaApp({
    page: page ? page : undefined,
    resolve: (name) => {
      const pageModule = pages[`../../../pages/${name}.tsx`]

      if (!pageModule) {
        throw new Error(`Page not found: ${name}`)
      }

      /**
       * Inertia v2 adapter:
       * в v2 default layout назначается через resolve()
       * и мутацию resolved page component.
       *
       * При миграции на v3 этот блок надо удалить
       * и заменить на createInertiaApp({ layout }).
       */
      const layout = getV2PageLayout(name)

      if (layout) {
        pageModule.default.layout ??= layout
      }

      return pageModule
    },
    setup({ el, App, props }) {
      const locale = props.initialPage?.props?.locale

      const root = createRoot(el)
      root.render(
        <StrictMode>
          <UIProvider>
            <I18nProvider locale={locale}>
              <App {...props} />
            </I18nProvider>
          </UIProvider>
        </StrictMode>,
      )
    },
  })
}
