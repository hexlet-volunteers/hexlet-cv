import { useTranslation } from 'react-i18next'
import { IconClock } from '@tabler/icons-react'
import { Group, Text } from '@mantine/core'

type TProps = {
  value: number
}

export const ReadingTime: React.FC<TProps> = (props) => {
  const { value } = props

  const { t } = useTranslation()

  const m = Math.max(1, Math.round(value))

  return (
    <Group gap={6} wrap="nowrap" align="center">
      <IconClock size={10} stroke={1.8} />
      <Text size="xs" c="dimmed" lh={1}>
        {t('articles.readingTime', { count: m })}
      </Text>
    </Group>
  )
}
