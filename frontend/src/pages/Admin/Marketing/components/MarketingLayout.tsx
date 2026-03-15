import { Container, Title, Group, Button } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import MarketingNavbar from './MarketingNavbar'

export interface PageProps {
  children: React.ReactNode
}

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
        <Button variant="default">{t('adminPage.marketing.buttonCreate')}</Button>
      </Group>
      {children}
    </Container>
  )
}
