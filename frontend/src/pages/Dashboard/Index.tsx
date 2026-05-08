import { Container, getGradient, useMantineTheme, Stack } from '@mantine/core'
import { Footer } from '@widgets/Footer'
import { Header } from '@widgets/Header'
import { AuthPanel } from '@widgets/auth-panel'

const DashboardIndex = () => {
  const theme = useMantineTheme()

  return (
    <Stack
      // mih="100vh"
      bg={getGradient(
        {
          deg: 135,
          from: 'black',
          to: '#00031a',
        },
        theme,
      )}
      justify="space-between"
    >
      <Header renderLogin={AuthPanel} />
      <Container size="xl">Dashboard</Container>
      <Footer />
    </Stack>
  )
}

export default DashboardIndex
