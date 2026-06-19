import { initInertia } from '@inertia/inertia.provider'
import { enableMocking } from '@mocks/enableMocking'

async function bootstrap() {
  await enableMocking()
  initInertia()
}

bootstrap()
