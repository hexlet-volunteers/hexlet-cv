import type React from 'react'

export type InertiaLayout = (_page: React.ReactNode) => React.ReactNode

export type InertiaPage<P = Record<string, unknown>> =
  React.ComponentType<P> & {
    layout?: InertiaLayout | ((_page: React.ReactNode) => React.ReactNode)
  }
