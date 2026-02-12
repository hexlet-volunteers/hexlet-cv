import { useDisclosure } from '@mantine/hooks'
import { useContext, createContext } from 'react'

/**
 * Описывает пропсы провайдера навигации.
 */
type TProps = {
  /** Дочерние компоненты, которым нужно состояние навигации. */
  children: React.ReactNode
}
const NavigationContext = createContext({
  opened: false,
  toggle: () => {},
})

/**
 * Предоставляет состояние открытия мобильной навигации в личном кабинете.
 */
export const NavigationProvider: React.FC<TProps> = ({ children }) => {
  const [opened, { toggle }] = useDisclosure(false)
  return (
    <NavigationContext.Provider value={{ opened, toggle }}>
      {children}
    </NavigationContext.Provider>
  )
}

/**
 * Возвращает состояние и методы управления навигацией личного кабинета.
 */
export const useNavbar = () => useContext(NavigationContext)
