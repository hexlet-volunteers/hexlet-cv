import { Text, ThemeIcon, Badge, Group, Card } from '@mantine/core'
import { type Feature } from '../../model/types'

const PALETTES = {
  light: {
    bg: 'rgb(255, 255, 255)',
    title: 'rgb(23, 23, 30)',
    text: 'rgb(74, 79, 89)',
    iconBg: 'rgb(234, 242, 254)',
    icon: 'rgb(17, 110, 245)',
  },
  dark: {
    bg: 'rgb(23, 23, 30)',
    title: 'white',
    text: 'rgb(154, 162, 180)',
    iconBg: 'rgba(103, 65, 217, 0.4)',
    icon: 'rgb(197, 179, 245)',
  },
} as const

const ICON_FLIP = { transform: 'scale(-1)' } as const

interface FeaturesCardProps extends Feature {
  mirrorIcon?: boolean
}

/**
 * Карточка преимущества платформы.
 *
 * Отображает одну фичу из конфигурации лендинга: иконку (опционально
 * отзеркаленную по горизонтали и вертикали), бейдж-отличие и заголовок с описанием.
 * Поддерживает светлый и тёмный акцентный варианты через палитру `PALETTES`.
 *
 * @param props - Свойства компонента, расширяющие интерфейс `Feature`.
 * @param props.mirrorIcon - Флаг для применения CSS-трансформации `scale(-1)` к иконке.
 * @returns Отрендеренная карточка `<Card>` Mantine.
 */
export function FeaturesCard({
  icon,
  title,
  text,
  isDark,
  badge,
  mirrorIcon,
}: FeaturesCardProps) {
  const IconComponent = icon
  const palette = PALETTES[isDark ? 'dark' : 'light']

  return (
    <Card withBorder radius="lg" px={26} py={24} bg={palette.bg}>
      <Group align="center" mb={14} gap={8}>
        <ThemeIcon size={38} radius="md" bg={palette.iconBg}>
          <IconComponent
            size={18}
            color={palette.icon}
            style={mirrorIcon ? ICON_FLIP : undefined}
          />
        </ThemeIcon>
        {badge && (
          <Badge
            size="md"
            radius="md"
            bg={palette.iconBg}
            c={palette.icon}
            styles={{
              label: {
                fontSize: 10,
                fontWeight: 800,
              },
            }}
          >
            {badge}
          </Badge>
        )}
      </Group>
      <Text fz={16.5} fw={700} c={palette.title} mb={7}>
        {title}
      </Text>
      <Text c={palette.text} fz={13.5}>
        {text}
      </Text>
    </Card>
  )
}
