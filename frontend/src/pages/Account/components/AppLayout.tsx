import { Header } from './Header'
import { Navbar } from './Navbar'
import { SectionLayout } from './SectionLayout'
import { Flex } from '@mantine/core'
import { Footer } from '@widgets/Footer'
import { NavigationProvider } from './NavigationProvider.tsx'
import React from 'react'
import { AuthPanel } from '@widgets/auth-panel'

/**
 * Описывает пропсы layout-а личного кабинета.
 */
type TProps = {
  /** Содержимое активного раздела личного кабинета. */
  children: React.ReactNode
}

/**
 * Формирует общий layout разделов личного кабинета с header, навигацией и footer.
 */
export const AppLayout: React.FC<TProps> = (props) => {
  const { children } = props

  return (
    <NavigationProvider>
      <Flex direction="column" mih="100vh">
        <Header renderLogin={AuthPanel} />
        {/* на большом дисплее 4к контент размазывается, поэтому ограничили ширину 1600px*/}
        <Flex w="100%" flex={1} maw={1600} align="stretch" mx="auto">
          <Navbar />
          <SectionLayout>{children}</SectionLayout>
        </Flex>
        <Footer />
      </Flex>
    </NavigationProvider>
  )
}
