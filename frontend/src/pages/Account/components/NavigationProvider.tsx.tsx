import { useDisclosure } from '@mantine/hooks'
import { useContext, createContext } from 'react'

type TProps = {
  children: React.ReactNode
}
const NavigationContext = createContext({
  opened: false,
  toggle: () => {},
})

export const NavigationProvider: React.FC<TProps> = ({ children }) => {
  const [opened, { toggle }] = useDisclosure(false)
  return (
    <NavigationContext.Provider value={{ opened, toggle }}>
      {children}
    </NavigationContext.Provider>
  )
}
export const useNavbar = () => useContext(NavigationContext)
