import React from 'react'
import { Flex, Box } from '@mantine/core'
import { AdminNavbar } from './AdminNavbar'
import { AdminHeader } from './AdminHeader'

type TProps = {
  children: React.ReactNode
}

export const AdminLayout: React.FC<TProps> = ({ children }) => {
  return (
    <Flex direction="column" mih="100vh">
      <AdminHeader />
      <Flex style={{ flex: 1 }}>
        <AdminNavbar />
        <Box style={{ flex: 1 }}>
          {children}
        </Box>
      </Flex>
    </Flex>
  )
}
