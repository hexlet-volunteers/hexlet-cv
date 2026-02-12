import { Text } from '@mantine/core'
import classes from './AdminNavbar.module.css'
import { Link, usePage } from '@inertiajs/react'
import {
  IconSpeakerphone,
  IconVideo,
  IconBooks,
  IconMicrophone,
  IconStar,
  IconSchool,
  IconUsers,
  IconSettings,
  IconHelp,
  type IconProps,
} from '@tabler/icons-react'
import type { ForwardRefExoticComponent, RefAttributes } from 'react'

const iconsMap: Record<
  string,
  ForwardRefExoticComponent<IconProps & RefAttributes<SVGSVGElement>>
> = {
  IconSpeakerphone: IconSpeakerphone,
  IconVideo: IconVideo,
  IconBooks: IconBooks,
  IconMicrophone: IconMicrophone,
  IconStar: IconStar,
  IconSchool: IconSchool,
  IconUsers: IconUsers,
  IconSettings: IconSettings,
  IconHelp: IconHelp,
}

/**
 * Описывает один пункт административного меню.
 */
export type AdminMenuItemDTO = {
  /** Отображаемое название пункта меню. */
  label: string
  /** URL перехода в административный раздел. */
  link: string
  /** Ключ иконки из карты `iconsMap`. */
  icon: string
}

/**
 * Описывает группу пунктов административного меню.
 */
export type AdminMenuDTO = {
  /** Название группы пунктов меню. */
  category: string
  /** Список пунктов внутри группы. */
  items: AdminMenuItemDTO[]
}

/**
 * Отображает навигацию по административным разделам.
 */
export const AdminNavbar: React.FC = (): JSX.Element => {
  const { url, props } = usePage()
  const menuData = props.adminMenu as AdminMenuDTO[]

  return (
    <nav className={classes.navbar}>
      {menuData?.map((group) => (
        <div className={classes.section} key={group.category}>
          <Text c="dimmed" size="xs" pl="sm">
            {group.category}
          </Text>
          {group.items?.map((item) => {
            const isActive = url.includes(item.link)
            const IconComponent = iconsMap[item.icon]
            return (
              <Link
                key={item.label}
                className={classes.link}
                href={item.link}
                data-active={isActive || undefined}
              >
                <IconComponent className={classes.linkIcon} stroke={1.5} />
                <span>{item.label}</span>
              </Link>
            )
          })}
        </div>
      ))}
    </nav>
  )
}
