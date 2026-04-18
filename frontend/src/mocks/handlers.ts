import { handlers as authHandlers } from '@mocks/auth'
import { handlers as homeHadnlers } from '@mocks/home'
import { handlers as accountHadnlers } from '@mocks/account'
import { handlers as usersHadnlers } from '@mocks/users'
import { adminHandlers } from '@mocks/admin'

export const handlers = [...authHandlers, ...homeHadnlers, ...usersHadnlers, ...accountHadnlers, ...adminHandlers]
