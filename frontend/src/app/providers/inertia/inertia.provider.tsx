import { createInertiaApp } from '@inertiajs/react'
import { createRoot } from 'react-dom/client'
import { UIProvider } from '@ui/ui.provider'
import I18nProvider from '@i18n/i18n.provider'
import { StrictMode } from 'react'
import type { Page } from 'node_modules/@inertiajs/core/types/types'

export const initInertia = (page: Page) => {
  createInertiaApp({
    page,
    resolve: (name) => {
      console.log('Inertia ищет компонент:', name)
      const pages = import.meta.glob('../../../pages/**/*.tsx', { eager: true })
      const page = pages[`../../../pages/${name}.tsx`]
      if (!page) throw new Error(`Page not found: ${name}`)

      return page
    },
    setup({ el, App, props }) {
      const root = createRoot(el)
      root.render(
        <StrictMode>
          <UIProvider>
            <I18nProvider>
              <App {...props} />
            </I18nProvider>
          </UIProvider>
        </StrictMode>
      )
    },
  })
}
