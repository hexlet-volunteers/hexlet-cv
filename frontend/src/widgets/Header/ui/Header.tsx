import { Group, Divider, ThemeIcon, Text, Anchor } from '@mantine/core'
import { Link } from '@inertiajs/react'

const links = [
  {
    link: '#',
    label: 'Коммерческий опыт',
  },
  {
    link: '#',
    label: 'Аналитика',
  },
  {
    link: '/account/my-progress',
    label: 'Обучение', //временная ссылка для проверки работы личного кабинета раздела Мое обучение
  },
  {
    link: '#',
    label: 'Грейды',
  },
  {
    link: '#',
    label: 'База знаний',
  },
  {
    link: '#',
    label: 'Вебинары',
  },
  {
    link: '#',
    label: 'Кейсы',
  },
  {
    link: '#',
    label: 'Тарифы',
  },
  {
    link: '#',
    label: 'Новости',
  },
  {
    link: '#',
    label: 'Сообщество',
  },
]
interface Props {
  renderLogin: () => JSX.Element
}

export function Header(props: Props) {
  const { renderLogin } = props

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
              Hexlet
              <br />
              Карьера
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
