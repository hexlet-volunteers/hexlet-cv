import { Container, Title, Group, Button } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import MarketingNavbar from './MarketingNavbar'

/**
 * Пропсы для компонента MarketingLayout.
 */
export interface PageProps {
  /** Дочерние элементы, которые будут отображены внутри макета. */
  children: React.ReactNode
}

/**
 * Компонент макета для страниц управления маркетингом в админ-панели.
 * Включает заголовок, навигационную панель и кнопку создания нового элемента.
 *
 * @param props - Пропсы компонента.
 * @param props.children - Дочерние элементы для отображения внутри макета.
 * @returns React-компонент с макетом страницы маркетинга.
 */
export const MarketingLayout: React.FC<PageProps> = (props) => {
  const { children } = props
  const { t } = useTranslation()

  return (
    <Container size="fluid" py="md">
      <Title order={2} mb="md" fw={500}>
        {t('adminPage.marketing.title')}
      </Title>
      <Group gap="xs" mb="md" justify="space-between">
        <MarketingNavbar />
        <Button variant="default">
          {t('adminPage.marketing.buttonCreate')}
        </Button>
      </Group>
      {children}
    </Container>
  )
}
