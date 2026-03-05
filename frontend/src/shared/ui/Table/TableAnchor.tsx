import React from 'react'
import { Table, Anchor } from '@mantine/core'

interface TProps {
  href?: string
  children: React.ReactNode
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
  underline?: 'always' | 'hover' | 'not-hover' | 'never'
  tdProps?: React.ComponentProps<typeof Table.Td>
  anchorProps?: React.ComponentProps<typeof Anchor>
}

export const TableAnchor = (props: TProps) => {
  const { href, children, size = 'md', underline = 'not-hover', tdProps = {}, anchorProps = {}, ...rest } = props

  return (
    <Table.Td {...tdProps}>
      <Anchor
        href={href}
        size={size}
        underline={underline}
        {...anchorProps}
        {...rest}
      >
        {children}
      </Anchor>
    </Table.Td>
  )
}
