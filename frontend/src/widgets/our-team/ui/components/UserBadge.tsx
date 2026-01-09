import type { FC } from 'react'
import { Text, Avatar, Paper } from '@mantine/core'

interface UserBadgeProps {
  name: string
  role: string
}

export const UserBadge: FC<UserBadgeProps> = ({ name, role }) => {
  return (
    <Paper radius="lg" p="xs" >
      <Avatar src="" size={120} radius={120} mx="auto" />
      <Text ta="center" fz="lg" fw={500} mt="md">
        {name}
      </Text>
      <Text ta="center" fz="sm">
        {role}
      </Text>
    </Paper>
  )
}
