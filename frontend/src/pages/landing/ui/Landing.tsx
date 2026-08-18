import { Text, Box, Divider } from '@mantine/core'
import { Features } from './sections/Features'
import { How } from './sections/How'
import { Pricing } from './sections/Pricing'
import { Hero } from './sections/Hero'

export function Landing() {
  return (
    <Box>
      <Box bg="blue.6" c="white" ta="center" py={8}>
        <Text size="sm">
          Выпускникам Хекслета 2026 года тариф «Про» — бесплатно на 6 месяцев →
        </Text>
      </Box>

      <Hero />

      <Divider color="rgb(230, 232, 236)" />

      <Features />

      <Divider color="rgb(230, 232, 236)" />

      <How />

      <Pricing />
    </Box>
  )
}
