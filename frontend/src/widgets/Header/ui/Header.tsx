import { Group, Divider, ThemeIcon, Text, Anchor } from '@mantine/core'
import { Link } from '@inertiajs/react'
import { useTranslation } from 'react-i18next'

interface Props {
  renderLogin: () => JSX.Element
}

export function Header(props: Props) {
  const { renderLogin } = props
  const { t } = useTranslation()

  const links = [
    {
      link: '#',
      label: t('header.links.commercialExperience'),
    },
    {
      link: '#',
      label: t('header.links.analytics'),
    },
    {
      link: '/account/purchase',
      label: t('header.links.training'),
    },
    {
      link: '#',
      label: t('header.links.grades'),
    },
    {
      link: '#',
      label: t('header.links.knowledgeBase'),
    },
    {
      link: '#',
      label: t('header.links.webinars'),
    },
    {
      link: '#',
      label: t('header.links.cases'),
    },
    {
      link: '#',
      label: t('header.links.pricing'),
    },
    {
      link: '#',
      label: t('header.links.news'),
    },
    {
      link: '#',
      label: t('header.links.community'),
    },
  ]

  const items = links.map(
    (link): JSX.Element => (
      <Anchor
        key={link.label}
        href={link.link}
        variant="transparent"
        underline="never"
        component={Link}
        size="md"
        fw={500}
      >
        {link.label}
      </Anchor>
    )
  )

  return (
    <header>
      <Group py={10} justify="center" gap={70} bg="black">
        <Anchor
          key="Home"
          href="/" // В данный момент ссылка на главную страницу
          variant="transparent"
          underline="never"
          size="md"
          fw={500}
        >
          <Group gap="xs">
            <ThemeIcon variant="white" size="lg" radius="md">
              <Text c="black" size="md" fw={500}>
                X
              </Text>
            </ThemeIcon>
            <Text c="white" size="md" fw={700} lh={1.1}>
              {t('header.brand.line1')}
              <br />
              {t('header.brand.line2')}
            </Text>
          </Group>
        </Anchor>
        <nav>
          <Group gap="xl">{items}</Group>
        </nav>
        {renderLogin()}
      </Group>
      <Divider size={2} />
    </header>
  )
}
