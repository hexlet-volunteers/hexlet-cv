import {
  Paper,
  Text,
  Group,
  Button,
} from '@mantine/core'
import { Link } from '@inertiajs/react'
import { IconArrowRight } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import type { KnowledgeBaseDTO } from '@entities/account-knowledge-base'

/**
 * Интерфейс пропсов компонента KnowledgeBaseCard.
 *
 * @prop card - объект с информацией о статье или видео для базы знаний пользователя { id, title, duration, type, url }.
 */
interface KnowledgeBaseCardProps {
  card: KnowledgeBaseDTO,
}

/**
 * Карточка для отображения информации о статье или видео в базе знаний пользователя.
 * Рендерит карточку с названием статьи или видео, их продолжительностью и кнопкой "Читать" с ссылкой на материал.
 *
 * @param props - объект с информацией о статье или видео для базы знаний пользователя { id, title, duration, type, url }.
 */
export const KnowledgeBaseCard: React.FC<KnowledgeBaseCardProps> = (props) => {
  const { card } = props
  const { t } = useTranslation()
  return (
    <Paper
      radius="lg"
      withBorder
      p="lg"
    >
      <Text size="lg" fw={500}>
        {card.title}
      </Text>
      <Group gap="xs">
        <Text size="sm">{card.type}</Text>
        <Text size="sm">·</Text>
        <Text size="sm">{card.duration}</Text>
      </Group>
      <Button
        variant="default"
        radius="md"
        mt="md"
        component={Link}
        href={card.url}
        rightSection={<IconArrowRight size={20}/>}
      >
        {t('accountPage.knowledgeBase.readButton')}
      </Button>
    </Paper>
  )
}
