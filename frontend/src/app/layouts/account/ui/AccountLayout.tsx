import type { ReactNode } from 'react'

import { Container, Flex, Stack } from '@mantine/core'

import { Footer } from '@widgets/Footer'
import { AuthPanel } from '@widgets/auth-panel'
import {
  AccountHeader,
  AccountNavbar,
  AccountNavigationProvider,
} from '@widgets/account-navigation'
import { AccountActivityCards } from '@widgets/account-activity-cards'

import classes from './AccountLayout.module.css'

type AccountLayoutProps = {
  children: ReactNode
}

export const AccountLayout = ({ children }: AccountLayoutProps) => {
  return (
    <AccountNavigationProvider>
      <Flex direction="column" mih="100vh">
        <AccountHeader renderLogin={() => <AuthPanel />} />

        <Flex w="100%" maw="1600px" mx="auto" flex={1}>
          <AccountNavbar />

          <Container component="main" fluid pt="md" flex={1}>
            <Stack gap="md">
              <AccountActivityCards />

              <section className={classes.section}>{children}</section>
            </Stack>
          </Container>
        </Flex>

        <Footer />
      </Flex>
    </AccountNavigationProvider>
  )
}
