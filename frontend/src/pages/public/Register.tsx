import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router'
import {
  Alert,
  Anchor,
  Button,
  Checkbox,
  Container,
  Divider,
  Group,
  Paper,
  PasswordInput,
  Stack,
  Text,
  TextInput,
  Title,
} from '@mantine/core'
import { GithubButton, useAuth, type RegisterInput } from '@features/auth'
import { ApiError, type FieldErrors } from '@shared/api'

const empty: RegisterInput = {
  email: '',
  password: '',
  firstName: '',
  lastName: '',
  terms: false,
}

export function Register() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState<RegisterInput>(empty)
  const [errors, setErrors] = useState<FieldErrors>({})
  const [loading, setLoading] = useState(false)

  const set =
    (field: keyof RegisterInput) =>
    (e: { currentTarget: { value: string; checked: boolean; type: string } }) => {
      // Значение читаем сразу: к моменту вызова апдейтера React уже обнулит currentTarget.
      const { type, checked, value } = e.currentTarget
      setForm((f) => ({ ...f, [field]: type === 'checkbox' ? checked : value }))
    }

  const submit = async (e: FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setErrors({})
    try {
      await register(form)
      navigate('/app/resumes', { replace: true })
    } catch (err) {
      setErrors(err instanceof ApiError ? err.errors : { error: 'Не удалось зарегистрироваться' })
    } finally {
      setLoading(false)
    }
  }

  // Ошибки уровня формы (например, пароль похож на email) приходят с ключом DTO.
  const common = errors.error ?? errors.registrationRequestDTO

  return (
    <Container size={460} py={80}>
      <Title order={2} ta="center" mb="lg">
        Регистрация
      </Title>
      <Paper withBorder shadow="sm" p="xl" radius="md">
        <form onSubmit={submit} noValidate>
          <Stack>
            {common && <Alert color="red">{common}</Alert>}
            <GithubButton label="Продолжить через GitHub" />
            <Divider label="или по email" labelPosition="center" />
            <Group grow align="flex-start">
              <TextInput
                label="Имя"
                autoComplete="given-name"
                value={form.firstName}
                onChange={set('firstName')}
                error={errors.firstName}
                required
              />
              <TextInput
                label="Фамилия"
                autoComplete="family-name"
                value={form.lastName}
                onChange={set('lastName')}
                error={errors.lastName}
                required
              />
            </Group>
            <TextInput
              label="Email"
              placeholder="you@example.com"
              type="email"
              autoComplete="email"
              value={form.email}
              onChange={set('email')}
              error={errors.email}
              required
            />
            <PasswordInput
              label="Пароль"
              description="Минимум 8 символов"
              placeholder="Придумайте пароль"
              autoComplete="new-password"
              value={form.password}
              onChange={set('password')}
              error={errors.password}
              required
            />
            <Checkbox
              label="Принимаю условия использования и политику обработки персональных данных"
              checked={form.terms}
              onChange={set('terms')}
              error={errors.terms}
            />
            <Button type="submit" fullWidth loading={loading}>
              Создать аккаунт
            </Button>
            <Text size="sm" ta="center" c="dimmed">
              Уже есть аккаунт?{' '}
              <Anchor component={Link} to="/login">
                Войти
              </Anchor>
            </Text>
          </Stack>
        </form>
      </Paper>
    </Container>
  )
}
