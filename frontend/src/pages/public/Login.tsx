import { useState, type FormEvent } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router'
import {
  Alert,
  Anchor,
  Button,
  Container,
  Paper,
  PasswordInput,
  Stack,
  Text,
  TextInput,
  Title,
} from '@mantine/core'
import { useAuth } from '@features/auth'
import { ApiError, type FieldErrors } from '@shared/api'

/** Куда вернуть после входа: только внутренние пути, чтобы `?next=` не увёл на чужой сайт. */
function safeNext(next: string | null, fallback = '/app/resumes') {
  return next && next.startsWith('/') && !next.startsWith('//') ? next : fallback
}

export function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [errors, setErrors] = useState<FieldErrors>({})
  const [loading, setLoading] = useState(false)

  const submit = async (e: FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setErrors({})
    try {
      await login(email, password)
      navigate(safeNext(params.get('next')), { replace: true })
    } catch (err) {
      setErrors(err instanceof ApiError ? err.errors : { error: 'Не удалось войти' })
    } finally {
      setLoading(false)
    }
  }

  const common = errors.error ?? errors.auth

  return (
    <Container size={420} py={80}>
      <Title order={2} ta="center" mb="lg">
        Вход
      </Title>
      <Paper withBorder shadow="sm" p="xl" radius="md">
        <form onSubmit={submit} noValidate>
          <Stack>
            {common && <Alert color="red">{common}</Alert>}
            <TextInput
              label="Email"
              placeholder="you@example.com"
              type="email"
              autoComplete="email"
              value={email}
              onChange={(e) => setEmail(e.currentTarget.value)}
              error={errors.email}
              required
            />
            <PasswordInput
              label="Пароль"
              placeholder="Ваш пароль"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.currentTarget.value)}
              error={errors.password}
              required
            />
            <Anchor component={Link} to="/password/forgot" size="sm" mt={-8}>
              Забыли пароль?
            </Anchor>
            <Button type="submit" fullWidth loading={loading}>
              Войти
            </Button>
            <Text size="sm" ta="center" c="dimmed">
              Нет аккаунта?{' '}
              <Anchor component={Link} to="/register">
                Регистрация
              </Anchor>
            </Text>
          </Stack>
        </form>
      </Paper>
    </Container>
  )
}
