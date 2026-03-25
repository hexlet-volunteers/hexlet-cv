import { initInertia } from '@inertia/inertia.provider'
import { enableMocking } from '@mocks/enableMocking'

async function getInitialPage() {
  const res = await fetch(window.location.href, {
    headers: {
      'X-Inertia': 'true',
      'X-Requested-With': 'XMLHttpRequest',
      'Accept': 'application/json',
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to load initial Inertia page: ${res.status}`)
  }

  return res.json()
}

async function bootstrap() {
  await enableMocking()

  const page = await getInitialPage()

  initInertia(page)
}

bootstrap()
