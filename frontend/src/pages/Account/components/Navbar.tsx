import * as React from 'react'
import classes from './Navbar.module.css'
import { Drawer, Group, Stack } from '@mantine/core'
import { Link, usePage } from '@inertiajs/react'
import { normalizePathname } from '@shared/lib/helpers/normalizePathname'
import { useNavbar } from './NavigationProvider.tsx'

export const Navbar: React.FC = React.memo(() => {
  const { props: pageProps, url } = usePage()
  const { menu } = pageProps
  const { opened, toggle: navbarToggle } = useNavbar()

  const ativeMenu =
    menu?.find(
      ({ link }) => normalizePathname(link) === normalizePathname(url),
    ) || menu[0]

  const links =
    menu?.map((item) => (
      <Link
        className={classes.link}
        data-active={item.label === ativeMenu?.label || undefined}
        href={item.link}
        key={item.label}
      >
        {item.icon && <item.icon className={classes.linkIcon} stroke={1.5} />}
        <span>{item.label}</span>
      </Link>
    )) || []

  return (
    <>
      <nav className={classes.navbar}>
        <Group visibleFrom="md">
          <div className={classes.navbarMain}>{links}</div>
        </Group>
      </nav>
      <Drawer
        opened={opened}
        onClose={navbarToggle}
        title="Меню"
        padding="md"
        size="xs"
        hiddenFrom="md"
      >
        <Stack gap="md">{links}</Stack>
      </Drawer>
    </>
  )
})
