import { Button, Flex } from '@mantine/core'
import { Link } from 'react-router'
import { useAuth } from '@features/auth'

/**
 * Кнопки авторизации в шапке.
 *
 * Гостю — «Войти» и «Начать бесплатно», вошедшему — переход к резюме в кабинете и «Выйти».
 * Пока сессия проверяется, показываем гостевой вариант: так шапка не прыгает у большинства
 * посетителей лендинга.
 */
export function HeaderButtonGroup() {
  const { user, logout } = useAuth()

  if (user) {
    return (
      <Flex gap={{ base: 'xs', sm: 16 }}>
        <Button
          component={Link}
          to="/app/resumes"
          px={{ base: 10, sm: 'md' }}
          fz={13.5}
          fw={700}
          bg="rgb(17, 110, 245)"
        >
          Мои резюме
        </Button>
        <Button
          variant="subtle"
          c="rgb(62, 67, 77)"
          px={{ base: 10, sm: 'md' }}
          fz={13.5}
          fw={700}
          onClick={() => void logout()}
        >
          Выйти
        </Button>
      </Flex>
    )
  }

  return (
    <Flex gap={{ base: 'xs', sm: 28 }}>
      <Button
        component={Link}
        to="/login"
        variant="subtle"
        c="rgb(62, 67, 77)"
        px={{ base: 10, sm: 'md' }}
        fz={13.5}
        fw={700}
      >
        Войти
      </Button>
      <Button
        component={Link}
        to="/register"
        px={{ base: 10, sm: 'md' }}
        fz={13.5}
        fw={700}
        bg="rgb(17, 110, 245)"
      >
        Начать бесплатно
      </Button>
    </Flex>
  )
}
