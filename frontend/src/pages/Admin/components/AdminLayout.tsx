import React from 'react'
import { Flex, Box } from '@mantine/core'
import { AdminNavbar } from './AdminNavbar'
import { AdminHeader } from './AdminHeader'
import { AuthPanel } from '@widgets/auth-panel'

/**
 * Описывает пропсы layout-а административных страниц.
 */
type TProps = {
  /** Содержимое активного административного раздела. */
  children: React.ReactNode
}

/**
 * Формирует базовый layout административных страниц.
 */
export const AdminLayout: React.FC<TProps> = ({ children }) => {
  return (
    <Flex direction="column" mih="100vh">
      <AdminHeader renderLogin={AuthPanel} />
      <Flex style={{ flex: 1 }}>
        <AdminNavbar />
        <Box style={{ flex: 1 }}>{children}</Box>
      </Flex>
    </Flex>
  )
}
