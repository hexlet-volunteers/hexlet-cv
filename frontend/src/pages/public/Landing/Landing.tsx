import { Box } from '@mantine/core'
import { Hero } from './ui/Hero'
import { Features } from './ui/Features'
import { Ecosystem } from './ui/Ecosystem'
import { Commercial } from './ui/Commercial'
import { How } from './ui/How'
import { Pricing } from './ui/Pricing'
import { Cta } from './ui/Cta'

/**
 * Главная страница публичного сайта «Хекслет Карьера».
 *
 * Порядок секций и вся вёрстка — по прототипу
 * `docs/design/prototypes/site.dc.html` (эпик #1119). Секции самостоятельны и
 * ничего друг о друге не знают: каждая закрывает свою задачу из эпика, так что
 * их можно дорабатывать по отдельности.
 */
export { NewLanding as Landing } from './ui/redesign/NewLanding'

/** Прежняя главная по прототипу — оставлена для сравнения. */
export function LandingLegacy() {
  return (
    <Box>
      <Hero />
      <Features />
      <Ecosystem />
      <Commercial />
      <How />
      <Pricing />
      <Cta />
    </Box>
  )
}
