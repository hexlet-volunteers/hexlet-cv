import { useState } from 'react'
import { useForm } from '@inertiajs/react'
import {
  Container,
  Title,
  TextInput,
  PasswordInput,
  Button,
  Alert,
} from '@mantine/core'

export default function SignUp() {
  const [regError, setRegError] = useState<string | null>(null)
  const form = useForm({
    email: '',
    password: '',
    firstName: '',
    lastName: '',
  })

  const validateClient = () => {
    if (!/^\S+@\S+$/.test(form.data.email)) return 'Неверный email'
    if (form.data.password.length < 8)
      return 'Пароль должен быть минимум 8 символов'
    return null
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const err = validateClient()
    if (err) {
      setRegError(err)
      return
    }

    form.post('/en/users', {
      onError: (errs) => {
        console.error('Ошибка', errs)
        setRegError('Ошибка регистрации')
      },
      onSuccess: () => {
        console.log('Success!')
      },
    })
  }

  return (
    <Container size="sm" mt="xl">
      <Title order={2} ta="center">
        Регистрация
      </Title>
      <form onSubmit={handleSubmit}>
        <TextInput
          placeholder="Имя"
          mt="md"
          value={form.data.firstName}
          onChange={(e) => form.setData('firstName', e.currentTarget.value)}
        />
        <TextInput
          placeholder="Фамилия"
          mt="md"
          value={form.data.lastName}
          onChange={(e) => form.setData('lastName', e.currentTarget.value)}
        />
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
        {regError && (
          <Alert color="red" mt="sm">
            {regError}
          </Alert>
        )}
        <Button type="submit" fullWidth mt="md" loading={form.processing}>
          Зарегистрироваться
        </Button>
      </form>
    </Container>
  )
}
