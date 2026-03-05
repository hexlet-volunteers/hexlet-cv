import React from 'react'
import { Table, Text } from '@mantine/core'

interface TProps {
  value: string | number
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
  fw?: number | 'bold' | 'bolder' | 'normal' | 'lighter'
  ta?: 'left' | 'center' | 'right'
  tdProps?: React.ComponentProps<typeof Table.Td>
  textProps?: React.ComponentProps<typeof Text>
}

export const TableText = (props: TProps) => {
  const { value, size = 'md', fw, ta, tdProps = {}, textProps = {}, ...rest } = props

  return (
    <Table.Td {...tdProps}>
      <Text
        size={size}
        fw={fw}
        ta={ta}
        {...textProps}
        {...rest}
      >
        {value}
      </Text>
    </Table.Td>
  )
}
