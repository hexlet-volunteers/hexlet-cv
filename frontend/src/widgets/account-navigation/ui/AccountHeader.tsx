import { Autocomplete, Group, Burger } from '@mantine/core'
import classes from './AccountHeader.module.css'
import { IconSearch } from '@tabler/icons-react'
import { useAccountNavbar } from '../model/useAccountNavbar'

/**
 * Описывает пропсы header-а личного кабинета.
 */
interface AccountHeaderProps {
  /** Функция рендера блока авторизационных действий. */
  renderLogin: () => JSX.Element
}

/**
 * Отображает шапку личного кабинета с поиском и панелью авторизации.
 */
export const AccountHeader = (props: AccountHeaderProps) => {
  const { renderLogin } = props

  const { opened, toggle } = useAccountNavbar()

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
          {renderLogin()}
        </Group>
      </div>
    </header>
  )
}
