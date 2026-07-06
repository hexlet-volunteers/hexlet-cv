import { MantineProvider, createTheme } from '@mantine/core'
import '@mantine/core/styles.css'

// Чистая светлая тема «Хекслет Карьера» (Mantine во главе). Тёмный — только сайдбар.
const theme = createTheme({
  primaryColor: 'blue',
  defaultRadius: 'md',
  fontFamily: 'Inter, -apple-system, Segoe UI, Roboto, sans-serif',
})

export const UIProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  return (
    <MantineProvider
      theme={theme}
      defaultColorScheme="light"
      forceColorScheme="light"
    >
      {children}
    </MantineProvider>
  )
}
