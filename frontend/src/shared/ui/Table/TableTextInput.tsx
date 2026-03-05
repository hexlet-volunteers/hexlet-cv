import React from 'react'
import { Table, TextInput } from '@mantine/core'

interface TProps {
  value: string
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
  readOnly?: boolean
  tdProps?: React.ComponentProps<typeof Table.Td>
  textInputProps?: React.ComponentProps<typeof TextInput>
}

export const TableTextInput = (props: TProps) => {
  const { value, size = 'xs', readOnly = false, tdProps = {}, textInputProps = {}, ...rest } = props

  return (
    <Table.Td {...tdProps}>
      <TextInput
        value={value}
        size={size}
        readOnly={readOnly}
        {...textInputProps}
        {...rest}
      />
    </Table.Td>
  )
}
