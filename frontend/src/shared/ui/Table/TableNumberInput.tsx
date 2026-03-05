import React from 'react'
import { Table, NumberInput } from '@mantine/core'

interface TProps {
  value: number
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
  w?: string | number
  miw?: string | number
  maw?: string | number
  allowNegative?: boolean
  clampBehavior?: 'strict' | 'none'
  allowLeadingZeros?: boolean
  decimalScale?: number
  max?: number
  tdProps?: React.ComponentProps<typeof Table.Td>
  numberInputProps?: React.ComponentProps<typeof NumberInput>
}

export const TableNumberInput = (props: TProps) => {
  const {
    value,
    size = 'xs',
    w = '100%',
    miw,
    maw,
    allowNegative = false,
    clampBehavior,
    allowLeadingZeros = false,
    decimalScale,
    max,
    tdProps = {},
    numberInputProps = {},
    ...rest
  } = props

  return (
    <Table.Td {...tdProps}>
      <NumberInput
        value={value}
        size={size}
        w={w}
        miw={miw}
        maw={maw}
        allowNegative={allowNegative}
        clampBehavior={clampBehavior}
        allowLeadingZeros={allowLeadingZeros}
        decimalScale={decimalScale}
        max={max}
        {...numberInputProps}
        {...rest}
      />
    </Table.Td>
  )
}
