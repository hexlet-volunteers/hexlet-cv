import { useState, type FormEvent } from 'react'
import { Link } from 'react-router'
import { Alert, Anchor, Button, Container, Paper, Stack, Text, TextInput, Title } from '@mantine/core'
import { api, ApiError, type FieldErrors } from '@shared/api'

/** Запрос ссылки на сброс пароля (#793). */
export function ForgotPassword() {
  const [email, setEmail] = useState('')
  const [errors, setErrors] = useState<FieldErrors>({})
  const [sent, setSent] = useState(false)
  const [loading, setLoading] = useState(false)

  const submit = async (e: FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setErrors({})
    try {
      await api<void>('/api/auth/password/forgot', { method: 'POST', body: { email } })
      setSent(true)
    } catch (err) {
      setErrors(err instanceof ApiError ? err.errors : { error: 'Не удалось отправить письмо' })
    } finally {
      setLoading(false)
    }
  }

  return (
    <Container size={420} py={80}>
      <Title order={2} ta="center" mb="lg">
        Восстановление пароля
      </Title>
      <Paper withBorder shadow="sm" p="xl" radius="md">
        {sent ? (
          <Stack>
            <Alert color="green">
              Если такой адрес зарегистрирован, мы отправили на него ссылку. Она действует два часа.
            </Alert>
            <Text size="sm" c="dimmed">
              Письма нет? Проверьте «Спам» или запросите ссылку ещё раз через минуту.
            </Text>
            <Anchor component={Link} to="/login" size="sm">
              Вернуться ко входу
            </Anchor>
          </Stack>
        ) : (
          <form onSubmit={submit} noValidate>
            <Stack>
              <Text size="sm" c="dimmed">
                Укажите email, с которым регистрировались, — пришлём ссылку для нового пароля.
              </Text>
              {errors.error && <Alert color="red">{errors.error}</Alert>}
              <TextInput
                label="Email"
                type="email"
                autoComplete="email"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.currentTarget.value)}
                error={errors.email}
                required
              />
              <Button type="submit" loading={loading} fullWidth>
                Отправить ссылку
              </Button>
              <Anchor component={Link} to="/login" size="sm" ta="center">
                Вспомнили пароль? Войти
              </Anchor>
            </Stack>
          </form>
        )}
      </Paper>
    </Container>
  )
}
