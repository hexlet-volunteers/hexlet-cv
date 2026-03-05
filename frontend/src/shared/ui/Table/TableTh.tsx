import React from 'react'
import { Table } from '@mantine/core'

interface TProps {
  children: React.ReactNode
  fz?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
  py?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
  w?: string | number
  ta?: 'left' | 'center' | 'right'
  thProps?: React.ComponentProps<typeof Table.Th>
}

export const TableTh = (props: TProps) => {
  const { children, fz = 'md', py = 'xs', w, ta = 'left', thProps = {}, ...rest } = props

  return (
    <Table.Th fz={fz} py={py} w={w} ta={ta} {...thProps} {...rest}>
      {children}
    </Table.Th>
  )
}
