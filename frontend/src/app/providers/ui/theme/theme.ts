import { Card, createTheme, Divider } from '@mantine/core'

export const theme = createTheme({
  components: {
    Card: Card.extend({
      styles: () => ({
        root: {
          '--paper-bg': 'var(--ds-surface-card)',
        },
      }),
    }),
    Divider: Divider.extend({
      styles: {
        root: {
          '--divider-color': 'var(--ds-divider-color)',
        } as React.CSSProperties,
      },
    }),
  },
})
