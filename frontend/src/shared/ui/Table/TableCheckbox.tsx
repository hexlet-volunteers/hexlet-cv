import React from 'react'
import { Table, Checkbox } from '@mantine/core'

interface TProps {
  checked: boolean
  ta?: 'right' | 'center' | 'left'
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
  readOnly?: boolean
  ariaLabel?: string
  tdProps?: React.ComponentProps<typeof Table.Td>
  checkboxProps?: React.ComponentProps<typeof Checkbox>
}

export const TableCheckbox = (props: TProps) => {
  const { checked, ta = 'center', size = 'xs', readOnly = false, ariaLabel, tdProps = {}, checkboxProps = {}, ...rest } = props

  return (
    <Table.Td ta={ta} {...tdProps}>
      <Checkbox
        checked={checked}
        size={size}
        readOnly={readOnly}
        aria-label={ariaLabel}
        {...checkboxProps}
        {...rest}
        style={{ display: 'inline-block' }}
      />
    </Table.Td>
  )
}
