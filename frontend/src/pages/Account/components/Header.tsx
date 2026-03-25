import { Autocomplete, Group, Burger } from '@mantine/core'
import classes from './Header.module.css'
import { IconSearch } from '@tabler/icons-react'
import { useNavbar } from './NavigationProvider.tsx'

export const Header: React.FC = () => {
  const { opened, toggle } = useNavbar()

  return (
    <header className={classes.header}>
      <div className={classes.inner}>
        <Group>Личный кабинет</Group>
        <Group>
          <Autocomplete
            className={classes.search}
            placeholder="Search"
            leftSection={<IconSearch size={16} stroke={1.5} />}
            data={[]}
            visibleFrom="xs"
          />
          <Burger opened={opened} onClick={toggle} hiddenFrom="md" size="sm" />
        </Group>
      </div>
    </header>
  )
}
