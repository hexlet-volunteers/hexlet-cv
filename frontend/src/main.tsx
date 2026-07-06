import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { RouterProvider } from 'react-router'
import { UIProvider } from '@ui/ui.provider'
import { router } from '@app/router'

const el = document.getElementById('root')
if (el) {
  createRoot(el).render(
    <StrictMode>
      <UIProvider>
        <RouterProvider router={router} />
      </UIProvider>
    </StrictMode>,
  )
}
