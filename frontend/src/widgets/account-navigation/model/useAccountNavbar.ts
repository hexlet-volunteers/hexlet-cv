import { useContext } from 'react'
import { AccountNavigationContext } from './AccountNavigationContext'

/**
 * Возвращает состояние и методы управления навигацией личного кабинета.
 */
export const useAccountNavbar = () => useContext(AccountNavigationContext)
