import { MantineProvider } from '@mantine/core'
import '@mantine/core/styles.css'
import { cssVariablesResolver } from './theme/cssVariablesResolver'
import { theme } from './theme/theme'

export const UIProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  return (
    <MantineProvider
      theme={theme}
      cssVariablesResolver={cssVariablesResolver}
      defaultColorScheme="dark"
    >
      {children}
    </MantineProvider>
  )
}
