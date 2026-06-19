import { useDisclosure } from '@mantine/hooks'
import { AccountNavigationContext } from './AccountNavigationContext'

/**
 * Описывает пропсы провайдера навигации.
 */
type TProps = {
  /** Дочерние компоненты, которым нужно состояние навигации. */
  children: React.ReactNode
}

/**
 * Предоставляет состояние открытия мобильной навигации в личном кабинете.
 */
export const AccountNavigationProvider: React.FC<TProps> = ({ children }) => {
  const [opened, { toggle }] = useDisclosure(false)
  return (
    <AccountNavigationContext.Provider value={{ opened, toggle }}>
      {children}
    </AccountNavigationContext.Provider>
  )
}
