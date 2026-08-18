import { Box, Container, SimpleGrid } from '@mantine/core'
import { features } from '../../model/landingConfig'
import { FeaturesCard } from './FeaturesCard'
import { SectionHeader } from '@shared/ui/SectionHeader'

/**
 * Секция лендинга с ключевыми преимуществами (фичами) платформы.
 *
 * Компонент отображает заголовок секции и сетку карточек,
 * сформированную на основе конфигурационных данных.
 *
 * @returns {JSX.Element} Отрендеренная секция преимуществ с оберткой `<Box>` и сеткой `<SimpleGrid>`.
 */
export function Features() {
  return (
    <Box component="section" bg="rgb(244, 245, 247)" py={64} id="features">
      <Container size="lg" px={24}>
        <SectionHeader
          title="Всё, что между «ищу работу» и «вышел на работу»"
          description="Один контекст на все шаги: вакансия знает про ваше резюме, письмо — про вакансию, тренировка — про завтрашний собес."
        />
        <SimpleGrid cols={{ base: 1, md: 2 }} spacing={14}>
          {features.map((feature) => (
            <FeaturesCard
              key={feature.id}
              {...feature}
              mirrorIcon={feature.id === 'tracker'}
            />
          ))}
        </SimpleGrid>
      </Container>
    </Box>
  )
}
