import { Header } from '@widgets/Header'
import { Navbar } from './Navbar'
import { SectionLayout } from './SectionLayout'
import { Flex } from '@mantine/core'
import { Footer } from '@widgets/Footer'
import React from 'react'
import { Login } from '@widgets/login'

type TProps = {
  children: React.ReactNode
}

export const AppLayout: React.FC<TProps> = (props) => {
  const { children } = props

  return (
    <Flex direction="column" mih="100vh">
      <Header renderLogin={Login} />
      <Flex align="stretch" style={{ flex: 1 }}>
        <Navbar />
        <SectionLayout>{children}</SectionLayout>
      </Flex>
      <Footer />
    </Flex>
  )
}
