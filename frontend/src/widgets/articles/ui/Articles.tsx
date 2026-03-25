import { DsCard } from '@shared/uikit/DsCard/DsCard'
import type { IArticle } from '../types'
import { Badge, Button, Center, Container, Group, SimpleGrid, Space, Text, Title } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { IconArrowRight } from '@tabler/icons-react'
import { ReadingTime } from './components/ReadingTime'

type TProps = {
  articles: IArticle[]
}

const COLS_CONFIG = {
  base: 1,
  md: 3,
}

export const Articles: React.FC<TProps> = (props) => {
  const { articles } = props

  const { t } = useTranslation()

  return (
    <Container size="lg" py="xl">
      <Center>
        <Badge
          color="green"
          fz="xs"
          radius="xl"
          size="lg"
          variant="light"
          tt="none"
          styles={{
            root: {
              fontWeight: 'normal',
            },
          }}
        >
          {t('articles.title')}
        </Badge>
      </Center>
      <Space h="md" />
      <Center>
        <Title order={1}>
          {t('articles.description')}
        </Title>
      </Center>
      <Space h="md" />
      <SimpleGrid cols={COLS_CONFIG} spacing="lg">
        {articles.map(({ readingTime, tags, title }, index) => (
          <DsCard key={index}>
            <DsCard.Image />
            <DsCard.Content>
              <Group gap="8" mt="md">{tags.map((tag, index) => <Badge color="gray" fz="8" key={index} size="sm" tt="none" variant="light">{tag}</Badge>)}</Group>
              <Space h="xs" />
              <Text>{title}</Text>
              <Space h="xs" />
              <Group justify="space-between">
                <ReadingTime value={readingTime} />
                <Button component="a" rightSection={<IconArrowRight size={14} />} size="compact-xs" variant="transparent">{t('articles.readNextActionLabel')}</Button>
              </Group>
            </DsCard.Content>
          </DsCard>
        ))}
      </SimpleGrid>
    </Container>
  )
}
