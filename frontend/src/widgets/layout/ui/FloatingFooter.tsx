import {
  Affix,
  Anchor,
  Group,
  Title,
  Burger,
  Popover,
  Stack,
  rem,
} from '@mantine/core'
import { useDisclosure } from '@mantine/hooks'
import { NavLink } from 'react-router'

export function FloatingFooter() {
  const [opened, { toggle, close }] = useDisclosure(false)

  const links = [
    { label: 'Главная', to: '/' },
    { label: '404', to: '/404' },
    { label: 'Правовая', to: '/legal' },
    { label: 'Блог', to: '/blog' },
    { label: 'Админка ↗', to: '/admin', isExternal: true },
  ]

  const baseLinkStyle = {
    padding: '7px 13px',
    fontSize: 12,
    fontWeight: 700,
    textDecoration: 'none',
    display: 'block',
    borderRadius: 'var(--mantine-radius-lg)',
  }

  const getLinkStyle = ({ isActive }: { isActive: boolean }) => ({
    ...baseLinkStyle,
    backgroundColor: isActive ? 'rgb(17, 110, 245)' : 'transparent',
    color: isActive ? 'white' : 'var(--mantine-color-gray-5)',
  })

  return (
    <Affix position={{ bottom: 18, right: 18 }}>
      <Popover opened={opened} onClose={close} position="top-end" offset={2}>
        <Popover.Target>
          <Burger
            hiddenFrom="md"
            opened={opened}
            onClick={toggle}
            size="sm"
            bg="rgba(18, 18, 25, 0.8)"
            p="sm"
            color="white"
            style={{
              borderRadius: 'var(--mantine-radius-lg)',
              width: 44,
              height: 44,
            }}
          />
        </Popover.Target>

        <Popover.Dropdown
          bg="rgba(18, 18, 25, 0.8)"
          style={{ border: 'none', borderRadius: 'var(--mantine-radius-md)' }}
        >
          <Stack gap="xs" style={{ minWidth: 150 }}>
            <Title
              order={3}
              size={10}
              tt="uppercase"
              c="gray.6"
              ta="center"
              mb="xs"
              style={{ letterSpacing: rem(0.8) }}
            >
              Прототип
            </Title>
            {links.map((link) =>
              link.isExternal ? (
                <Anchor
                  key={link.label}
                  href={link.to}
                  target="_blank"
                  rel="noopener noreferrer"
                  onClick={close}
                  style={{
                    ...baseLinkStyle,
                    color: 'var(--mantine-color-gray-5)',
                  }}
                >
                  {link.label}
                </Anchor>
              ) : (
                <NavLink
                  key={link.label}
                  to={link.to}
                  onClick={close}
                  style={getLinkStyle}
                >
                  {link.label}
                </NavLink>
              ),
            )}
          </Stack>
        </Popover.Dropdown>
      </Popover>

      <Group
        visibleFrom="md"
        bg="rgb(23, 23, 30)"
        px={5}
        py={5}
        gap={2}
        style={{ borderRadius: 'var(--mantine-radius-xl)' }}
      >
        <Title order={3} size="xs" tt="uppercase" c="gray.7" px="xs">
          Прототип
        </Title>
        {links.map((link) =>
          link.isExternal ? (
            <Anchor
              key={link.label}
              href={link.to}
              target="_blank"
              rel="noopener noreferrer"
              style={{ ...baseLinkStyle, color: 'var(--mantine-color-gray-5)' }}
            >
              {link.label}
            </Anchor>
          ) : (
            <NavLink key={link.label} to={link.to} style={getLinkStyle}>
              {link.label}
            </NavLink>
          ),
        )}
      </Group>
    </Affix>
  )
}
