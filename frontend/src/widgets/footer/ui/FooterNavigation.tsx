import { Grid, Title, Stack, Anchor, rem } from '@mantine/core'
import { Link } from 'react-router'
import { sections } from '../model/footerConfig'
import { type NavSection, type NavLink } from '../model/types'

function FooterLink({ linkConfig }: { linkConfig: NavLink }) {
  if (linkConfig.isExternal) {
    return (
      <Anchor
        component="a"
        href={linkConfig.link}
        target="_blank"
        rel="noopener noreferrer"
        c="rgb(185, 190, 204)"
        fz={13}
      >
        {linkConfig.label}
      </Anchor>
    )
  }

  return (
    <Anchor
      component={Link}
      to={linkConfig.link}
      c="rgb(185, 190, 204)"
      fz={13}
    >
      {linkConfig.label}
    </Anchor>
  )
}

function NavigationSection({ section }: { section: NavSection }) {
  return (
    <Grid.Col span={{ base: 60, sm: 30, md: 14 }}>
      <Title
        order={3}
        size={11}
        tt="uppercase"
        c="rgb(95, 100, 114)"
        mb={14}
        style={{ letterSpacing: rem(1.2) }}
      >
        {section.title}
      </Title>
      <Stack align="flex-start" justify="flex-start" gap={6} w={240}>
        {section.links.map((link) => (
          <FooterLink key={link.label} linkConfig={link} />
        ))}
      </Stack>
    </Grid.Col>
  )
}

/**
 * Компонент навигационных колонок для подвала сайта.
 *
 * Автоматически рендерит группы ссылок (секции) на основе конфигурационного файла.
 * Использует внутренние компоненты для разделения внешних и внутренних ссылок,
 * а также для адаптивной разметки колонок через Mantine Grid.Col.
 *
 * @returns {JSX.Element} Фрагмент со списком колонок навигации.
 */
export function FooterNavigation() {
  return (
    <>
      {sections.map((section) => (
        <NavigationSection key={section.title} section={section} />
      ))}
    </>
  )
}
