import { Avatar } from '@mantine/core'
import { avatarColor } from '../lib/format'

export function CompanyAvatar({
  company,
  size = 34,
}: {
  company: string
  size?: number
}) {
  return (
    <Avatar
      size={size}
      radius="md"
      color={avatarColor(company)}
      variant="light"
    >
      {company.trim().charAt(0).toUpperCase() || '?'}
    </Avatar>
  )
}
