import { useState, type FormEvent } from 'react'
import { Link, useSearchParams } from 'react-router'
import { Alert, Anchor, Button, Container, Paper, PasswordInput, Stack, Title } from '@mantine/core'
import { api, ApiError, type FieldErrors } from '@shared/api'

/** Новый пароль по ссылке из письма. */
export function ResetPassword() {
  const [params] = useSearchParams()
  const token = params.get('token') ?? ''
  const [password, setPassword] = useState('')
  const [errors, setErrors] = useState<FieldErrors>({})
  const [done, setDone] = useState(false)
  const [loading, setLoading] = useState(false)

  const submit = async (e: FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setErrors({})
    try {
      await api<void>('/api/auth/password/reset', { method: 'POST', body: { token, password } })
      setDone(true)
    } catch (err) {
      setErrors(err instanceof ApiError ? err.errors : { error: 'Не удалось сменить пароль' })
    } finally {
      setLoading(false)
    }
  }

  const tokenError = errors.token ?? (!token ? 'В ссылке нет токена. Запросите новую.' : undefined)

  return (
    <Container size={420} py={80}>
      <Title order={2} ta="center" mb="lg">
        Новый пароль
      </Title>
      <Paper withBorder shadow="sm" p="xl" radius="md">
        {done ? (
          <Stack>
            <Alert color="green">Пароль изменён. Войдите с новым паролем.</Alert>
            <Button component={Link} to="/login" fullWidth>
              Войти
            </Button>
          </Stack>
        ) : (
          <form onSubmit={submit} noValidate>
            <Stack>
              {tokenError && (
                <Alert color="red">
                  {tokenError}{' '}
                  <Anchor component={Link} to="/password/forgot">
                    Запросить ссылку
                  </Anchor>
                </Alert>
              )}
              {errors.error && <Alert color="red">{errors.error}</Alert>}
              <PasswordInput
                label="Новый пароль"
                description="Минимум 8 символов"
                autoComplete="new-password"
                value={password}
                onChange={(e) => setPassword(e.currentTarget.value)}
                error={errors.password}
                required
              />
              <Button type="submit" loading={loading} disabled={!token} fullWidth>
                Сохранить пароль
              </Button>
            </Stack>
          </form>
        )}
      </Paper>
    </Container>
  )
}
