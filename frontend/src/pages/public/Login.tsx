import { Link } from 'react-router'
import { Container, Paper, Title, TextInput, PasswordInput, Button, Stack, Divider, Text, Anchor } from '@mantine/core'

export function Login() {
  return (
    <Container size={420} py={80}>
      <Title order={2} ta="center" mb="lg">Вход</Title>
      <Paper withBorder shadow="sm" p="xl" radius="md">
        <Stack>
          <Button variant="default" fullWidth>Войти через GitHub</Button>
          <Button variant="default" fullWidth>Войти через аккаунт Хекслета</Button>
          <Divider label="или по email" labelPosition="center" />
          <TextInput label="Email" placeholder="you@example.com" />
          <PasswordInput label="Пароль" placeholder="Ваш пароль" />
          <Button fullWidth component={Link} to="/app">Войти</Button>
          <Text size="sm" ta="center" c="dimmed">
            Нет аккаунта? <Anchor component={Link} to="/register">Регистрация</Anchor>
          </Text>
        </Stack>
      </Paper>
    </Container>
  )
}
