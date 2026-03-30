import { SignUpForm, SocialButtons } from '@features/auth'
import { Container } from '@mantine/core'
import { Header } from '@widgets/Header'
import { AuthPanel } from '@widgets/auth-panel'

/**
 * Отображает страницу регистрации пользователя с формой создания аккаунта.
 */
export default function SignUp() {
  return (
    <>
      <Header renderLogin={AuthPanel} />
      <Container size={420} my={40}>
        <SignUpForm socialAuth={<SocialButtons />} />
      </Container>
    </>
  )
}
