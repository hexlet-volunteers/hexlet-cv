import type { ReactNode } from 'react'

import { Box, Flex } from '@mantine/core'

import { AuthPanel } from '@widgets/auth-panel'
import { AdminHeader } from '@widgets/admin-header'
import { AdminNavbar } from '@widgets/admin-navigation'

type AdminLayoutProps = {
  children: ReactNode
}

export const AdminLayout = ({ children }: AdminLayoutProps) => {
  return (
    <Flex direction="column" mih="100vh">
      <AdminHeader renderLogin={() => <AuthPanel />} />

      <Flex flex={1}>
        <AdminNavbar />

        <Box component="main" flex={1}>
          {children}
        </Box>
      </Flex>
    </Flex>
  )
}
