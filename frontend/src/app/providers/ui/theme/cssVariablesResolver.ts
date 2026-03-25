import type { CSSVariablesResolver } from '@mantine/core'
import { tokens } from './tokens'

export const cssVariablesResolver: CSSVariablesResolver = () => ({
  variables: {},
  light: {
    '--ds-divider-color': 'rgba(255, 255, 255, 0.1)',
    '--ds-surface-card': tokens.light.surfaceCard,
    '--mantine-color-anchor': tokens.light.link,
    '--mantine-color-text': tokens.light.textPrimary,
  },
  dark: {
    '--ds-divider-color': 'rgba(255, 255, 255, 0.1)',
    '--ds-surface-card': tokens.light.surfaceCard,
    '--mantine-color-anchor': tokens.light.link,
    '--mantine-color-text': tokens.dark.textPrimary,
  },
})
