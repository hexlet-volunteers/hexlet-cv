import { type Icon } from '@tabler/icons-react'

export interface SocialIcon {
  id: string
  label: string
  link: string
  icon: Icon
}

export interface NavLink {
  label: string
  link: string
  isExternal?: boolean
}

export interface NavSection {
  title: string
  links: NavLink[]
}
