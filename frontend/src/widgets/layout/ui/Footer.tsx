import {
  Box,
  Group,
  Text,
  Title,
  Container,
  Anchor,
  ActionIcon,
  Stack,
  Divider,
  Grid,
  rem,
} from '@mantine/core'
import { Logo } from '@shared/ui/Logo'
import { FloatingFooter } from './FloatingFooter'
import { Link } from 'react-router'

interface SocialIcon {
  id: string
  label: string
  link: string
  path: string
}

interface NavLink {
  label: string
  link: string
  isExternal?: boolean
}

interface NavSection {
  title: string
  links: NavLink[]
}

const socialIcons: SocialIcon[] = [
  {
    id: 'tg',
    label: 'Telegram',
    link: 'https://t.me',
    path: 'M21.9 4.6c.2-1-.5-1.5-1.4-1.2L2.8 10.2c-1 .4-1 1.1-.2 1.4l4.5 1.4 10.4-6.6c.5-.3 1-.1.6.2l-8.4 7.6-.3 4.6c.5 0 .7-.2 1-.5l2.3-2.2 4.7 3.5c.9.5 1.5.2 1.7-.8z',
  },
  {
    id: 'vk',
    label: 'ВКонтакте',
    link: 'https://vk.com',
    path: 'M13 18.7c-5.7 0-9-3.9-9.1-10.4h2.9c.1 4.8 2.2 6.8 3.9 7.2V8.3h2.7v4.1c1.6-.2 3.4-2.1 4-4.1h2.7a7.9 7.9 0 0 1-3.6 5.1 8.2 8.2 0 0 1 4.3 5.3h-3c-.6-1.9-2.2-3.4-4.4-3.6v3.6z',
  },
  {
    id: 'yt',
    label: 'YouTube',
    link: 'https://youtube.com',
    path: 'M21.6 7.2a2.5 2.5 0 0 0-1.8-1.8C18.2 5 12 5 12 5s-6.2 0-7.8.4A2.5 2.5 0 0 0 2.4 7.2 26 26 0 0 0 2 12a26 26 0 0 0 .4 4.8 2.5 2.5 0 0 0 1.8 1.8c1.6.4 7.8.4 7.8.4s6.2 0 7.8-.4a2.5 2.5 0 0 0 1.8-1.8A26 26 0 0 0 22 12a26 26 0 0 0-.4-4.8zM10 15.2V8.8l5.2 3.2z',
  },
  {
    id: 'gh',
    label: 'GitHub',
    link: 'https://github.com/',
    path: 'M12 2a10 10 0 0 0-3.2 19.5c.5.1.7-.2.7-.5v-1.7c-2.8.6-3.4-1.4-3.4-1.4-.5-1.2-1.1-1.5-1.1-1.5-.9-.6.1-.6.1-.6 1 .1 1.5 1 1.5 1 .9 1.5 2.3 1.1 2.9.8.1-.6.3-1.1.6-1.3-2.2-.3-4.6-1.1-4.6-5 0-1.1.4-2 1-2.7-.1-.2-.4-1.2.1-2.6 0 0 .8-.3 2.7 1a9.4 9.4 0 0 1 5 0c1.9-1.3 2.7-1 2.7-1 .5 1.4.2 2.4.1 2.6.6.7 1 1.6 1 2.7 0 3.9-2.4 4.7-4.6 5 .3.3.7.9.7 1.8V21c0 .3.2.6.7.5A10 10 0 0 0 12 2z',
  },
]

const sections: NavSection[] = [
  {
    title: 'Продукт',
    links: [
      { label: 'Вакансии и матчинг', link: '/app/vacancies' },
      { label: 'Резюме с AI-ревью', link: '/app/resume' },
      { label: 'Трекер откликов', link: '/app/tracker' },
      { label: 'AI-собеседования', link: '/app/ai-interview' },
      { label: 'Тарифы', link: '/#pricing' },
    ],
  },
  {
    title: 'Хекслет',
    links: [
      { label: 'Курсы и профессии', link: '#', isExternal: true },
      { label: 'Коммерческие проекты', link: '/app/commercial-projects' },
      { label: 'Сообщество', link: '#', isExternal: true },
      { label: 'Блог', link: '/blog' },
      { label: 'База вопросов с собеседований', link: '#', isExternal: true },
      { label: 'Помощь', link: '#', isExternal: true },
    ],
  },
  {
    title: 'Документы',
    links: [
      { label: 'Правовая информация', link: '/legal' },
      { label: 'Персональные данные', link: '/legal' },
      { label: 'Оферта', link: '/legal' },
      {
        label: 'careers@hexlet.io',
        link: 'mailto:careers@hexlet.io',
        isExternal: true,
      },
    ],
  },
]

interface SocialLinkProps {
  icon: SocialIcon
}

function SocialLink({ icon }: SocialLinkProps) {
  return (
    <ActionIcon
      component="a"
      href={icon.link}
      target="_blank"
      rel="noopener noreferrer"
      variant="filled"
      color="gray.9"
      size="lg"
      radius="md"
      aria-label={`Мы в ${icon.label}`}
    >
      <svg width="15" height="15" viewBox="0 0 24 24" fill="#B9BECC">
        <path d={icon.path}></path>
      </svg>
    </ActionIcon>
  )
}

export function Footer() {
  const groups = sections.map((group: NavSection) => {
    const links = group.links.map((link: NavLink) => {
      if (link.isExternal) {
        return (
          <Anchor
            key={link.label}
            component="a"
            href={link.link}
            target="_blank"
            rel="noopener noreferrer"
            c="rgb(185, 190, 204)"
            fz={13}
          >
            {link.label}
          </Anchor>
        )
      }

      return (
        <Anchor
          key={link.label}
          component={Link}
          to={link.link}
          c="rgb(185, 190, 204)"
          fz={13}
        >
          {link.label}
        </Anchor>
      )
    })

    return (
      <Grid.Col key={group.title} span={{ base: 60, sm: 30, md: 14 }}>
        <Title
          order={3}
          size={11}
          tt="uppercase"
          c="rgb(95, 100, 114)"
          mb={14}
          style={{ letterSpacing: rem(1.2) }}
        >
          {group.title}
        </Title>
        <Stack align="flex-start" justify="flex-start" gap={6} w={240}>
          {links}
        </Stack>
      </Grid.Col>
    )
  })

  const socialLinks = socialIcons.map((icon: SocialIcon) => (
    <SocialLink icon={icon} key={icon.id} />
  ))

  return (
    <Box component="footer" pt={52} bg="rgb(23, 23, 30)">
      <Container size="lg" px={24}>
        <Grid
          component="nav"
          aria-label="Навигация в футере"
          gutter={36}
          pb={40}
          columns={60}
        >
          <Grid.Col span={{ base: 60, sm: 30, md: 18 }}>
            <Stack>
              <Anchor component={Link} to="/" underline="never">
                <Logo variant="light" />
              </Anchor>
              <Text display="inline-block" w={240} fz={12.5} c="gray.6">
                Сервис Хекслета для поиска работы в IT: от первого резюме до
                подписанного оффера.
              </Text>
              <Group gap="xs">{socialLinks}</Group>
            </Stack>
          </Grid.Col>
          {groups}
        </Grid>
        <Divider color="rgba(255, 255, 255, 0.09)" />
        <Group justify="flex-start" align="center" pt="md" pb="lg">
          <Text c="gray.6" size="xs">
            © 2012–2026 Хекслет · Программирование — это просто
          </Text>
        </Group>
        <FloatingFooter />
      </Container>
    </Box>
  )
}
