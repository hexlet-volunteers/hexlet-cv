import { handlers as homeHadnlers } from '@mocks/home'
import { handlers as accountHadnlers } from '@mocks/account'
import { adminHandlers } from '@mocks/admin'

export const handlers = [...homeHadnlers, ...accountHadnlers, ...adminHandlers]
