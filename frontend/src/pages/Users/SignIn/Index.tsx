import { SignInForm, SocialButtons } from '@features/auth'
import { Container } from '@mantine/core'
import { Header } from '@widgets/Header'
import { Login } from '@widgets/login'

export default function SignIn() {
  return (
    <>
      <Header renderLogin={Login} />
      <Container size={420} my={40}><SignInForm socialAuth={<SocialButtons />} /></Container>
    </>
  )
}
