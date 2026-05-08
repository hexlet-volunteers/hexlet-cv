import { createContext } from 'react'

export const AccountNavigationContext = createContext({
  opened: false,
  toggle: () => {},
})
