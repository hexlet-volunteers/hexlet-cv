import { Link } from 'react-router'
import { Container, Paper, Title, TextInput, PasswordInput, Button, Stack, Divider, Text, Anchor } from '@mantine/core'

export function Register() {
  return (
    <Container size={420} py={80}>
      <Title order={2} ta="center" mb="lg">Регистрация</Title>
      <Paper withBorder shadow="sm" p="xl" radius="md">
        <Stack>
          <Button variant="default" fullWidth>Продолжить через GitHub</Button>
          <Button variant="default" fullWidth>Войти через аккаунт Хекслета</Button>
          <Divider label="или по email" labelPosition="center" />
          <TextInput label="Email" placeholder="you@example.com" />
          <PasswordInput label="Пароль" description="Минимум 8 символов" placeholder="Придумайте пароль" />
          <Button fullWidth component={Link} to="/app">Создать аккаунт</Button>
          <Text size="sm" ta="center" c="dimmed">
            Уже есть аккаунт? <Anchor component={Link} to="/login">Войти</Anchor>
          </Text>
        </Stack>
      </Paper>
    </Container>
  )
}
