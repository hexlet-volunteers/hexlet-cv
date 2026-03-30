import { SignInForm, SocialButtons } from '@features/auth'
import { Container } from '@mantine/core'
import { Header } from '@widgets/Header'
import { AuthPanel } from '@widgets/auth-panel'

/**
 * Отображает страницу входа пользователя с формой авторизации и OAuth-кнопками.
 */
export default function SignIn() {
  return (
    <>
      <Header renderLogin={AuthPanel} />
      <Container size={420} my={40}>
        <SignInForm socialAuth={<SocialButtons />} />
      </Container>
    </>
  )
}
