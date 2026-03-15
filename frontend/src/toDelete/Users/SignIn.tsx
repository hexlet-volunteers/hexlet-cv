import { useState } from 'react'
import { useForm, Link } from '@inertiajs/react'
import {
  Container,
  Title,
  TextInput,
  PasswordInput,
  Button,
  Alert,
  Group,
} from '@mantine/core'

export default function SignIn() {
  const [loginError, setLoginError] = useState<string | null>(null)
  const form = useForm({
    email: '',
    password: '',
  })

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setLoginError(null)

    if (!/^\S+@\S+$/.test(form.data.email)) {
      setLoginError('Введите корректный email')
      return
    }

    form.post('/en/users/sign_in', {
      onError: (errs) => {
        console.error('Ошибка', errs)
        setLoginError('Ошибка входа. Проверьте данные.')
      },
      onSuccess: () => {
        console.log('Успешный вход!')
      },
    })
  }

  return (
    <Container size="sm" mt="xl">
      <Title order={2} ta="center">
        Вход
      </Title>
      <form onSubmit={handleSubmit}>
        <TextInput
          placeholder="E-mail"
          mt="md"
          value={form.data.email}
          onChange={(e) => form.setData('email', e.currentTarget.value)}
        />
        <PasswordInput
          placeholder="Пароль"
          mt="md"
          value={form.data.password}
          onChange={(e) => form.setData('password', e.currentTarget.value)}
        />
        {loginError && (
          <Alert color="red" mt="sm">
            {loginError}
          </Alert>
        )}
        <Button type="submit" fullWidth mt="md" loading={form.processing}>
          Войти
        </Button>
        <Group justify="center" mt="md">
          <Button component={Link} href="/en/users/sign_up" variant="subtle">
            Регистрация
          </Button>
        </Group>
      </form>
    </Container>
  )
}
