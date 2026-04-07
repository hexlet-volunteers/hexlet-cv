import axios from 'axios'
import { initInertia } from '@inertia/inertia.provider'
import { enableMocking } from '@mocks/enableMocking'

axios.defaults.withCredentials = true
axios.defaults.xsrfCookieName = 'XSRF-TOKEN'
axios.defaults.xsrfHeaderName = 'X-XSRF-TOKEN'

async function getInitialPage() {
  const res = await fetch(window.location.href, {
    credentials: 'include',
    headers: {
      'X-Inertia': 'true',
      'X-Requested-With': 'XMLHttpRequest',
      Accept: 'application/json',
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to load initial Inertia page: ${res.status}`)
  }

  return res.json()
}

async function bootstrap() {
  const mockEnabled = await enableMocking()

  const page = mockEnabled ? await getInitialPage() : null

  initInertia(page)
}

bootstrap()
