import { Burger, Button, Drawer, Anchor, Stack } from '@mantine/core'
import { useDisclosure } from '@mantine/hooks'
import { Link } from 'react-router'
import { Logo } from '@shared/ui/Logo'
import { links } from '../model/headerConfig'

/**
 * Компонент мобильного меню навигации («бургер»).
 *
 * Отображает кнопку-бургер, которая скрывается на экранах больше `sm`.
 * При клике открывает полноэкранное боковое меню (Drawer) со списком ссылок,
 * импортируемых из конфигурации заголовка. При клике на любую ссылку меню автоматически закрывается.
 *
 * @returns Элемент разметки интерфейса для мобильной навигации.
 */
export function HeaderBurgerMenu() {
  const [opened, { toggle }] = useDisclosure()
  return (
    <>
      <Burger
        opened={opened}
        onClick={toggle}
        aria-label="Показать меню"
        hiddenFrom="sm"
        color="rgb(62, 67, 77)"
      />
      {/* Меню перекрывает шапку целиком (zIndex выше, чем у sticky-шапки — 201),
          иначе шапка ложится поверх и прячет первый пункт и крестик меню. */}
      <Drawer
        opened={opened}
        onClose={toggle}
        size="100%"
        position="left"
        zIndex={300}
        title={<Logo variant="dark" />}
        transitionProps={{ transition: 'slide-right' }}
        styles={{
          header: {
            minHeight: 64,
            borderBottom: '1px solid var(--mantine-color-gray-2)',
          },
          close: { width: 40, height: 40 },
        }}
      >
        <Stack py={16} gap={0}>
          {links.map((link) => (
            <Anchor
              component={Link}
              to={link.link}
              c="rgb(23, 23, 30)"
              fz={20}
              fw={700}
              py={16}
              underline="never"
              onClick={toggle}
              key={link.label}
              style={{ borderBottom: '1px solid var(--mantine-color-gray-2)' }}
            >
              {link.label}
            </Anchor>
          ))}
        </Stack>
        <Stack gap={10} mt={24}>
          <Button
            component={Link}
            to="/register"
            size="lg"
            fz={15}
            bg="rgb(17, 110, 245)"
            onClick={toggle}
          >
            Начать бесплатно
          </Button>
          <Button
            component={Link}
            to="/login"
            size="lg"
            fz={15}
            variant="default"
            onClick={toggle}
          >
            Войти
          </Button>
        </Stack>
      </Drawer>
    </>
  )
}
