import type { ForwardRefExoticComponent, RefAttributes } from 'react'

import { Link, usePage } from '@inertiajs/react'
import { Text } from '@mantine/core'
import {
  IconBooks,
  IconHelp,
  IconMicrophone,
  IconSchool,
  IconSettings,
  IconSpeakerphone,
  IconStar,
  IconUsers,
  IconVideo,
  type IconProps,
} from '@tabler/icons-react'

import classes from './AdminNavbar.module.css'

const iconsMap: Record<
  string,
  ForwardRefExoticComponent<IconProps & RefAttributes<SVGSVGElement>>
> = {
  IconSpeakerphone,
  IconVideo,
  IconBooks,
  IconMicrophone,
  IconStar,
  IconSchool,
  IconUsers,
  IconSettings,
  IconHelp,
}

export const AdminNavbar = () => {
  const { url, props } = usePage()
  const menuData = props.adminMenu

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
                key={item.link}
                className={classes.link}
                href={item.link}
                data-active={isActive || undefined}
              >
                {IconComponent && (
                  <IconComponent className={classes.linkIcon} stroke={1.5} />
                )}
                <span>{item.label}</span>
              </Link>
            )
          })}
        </div>
      ))}
    </nav>
  )
}
