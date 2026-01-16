import {
  Container,
  Title,
  Text,
  SimpleGrid,
  Group,
  Stack,
  Badge
} from '@mantine/core'
import { UserBadge } from './components/UserBadge'
import { useTranslation } from 'react-i18next'

export const OurTeam: React.FC = () => {
  const { t } = useTranslation()

  const staticTeamMembers = [
    {
      name: 'Максим',
      role: 'Основатель сервиса',
    },
    {
      name: 'Альберт',
      role: 'Администратор',
    },
    {
      name: 'Таня',
      role: 'HR-менеджер',
    },
    {
      name: 'Слава',
      role: 'Карьерный консультант',
    },
    {
      name: 'Лера',
      role: 'Карьерный консультант',
    },
  ]

  return (
    <Container size="lg" py="lg">
      <Group justify="center" mb="xs">
        <Stack gap="xs" align="center">
          <Badge fz="xs" fw="normal" size="lg" variant="light" color="teal" tt="none">
            {t('homePage.ourTeam.aboutBadge')}
          </Badge>
            <Text size="xs" c="lime" fw="bold">
              {t('homePage.ourTeam.aboutBadge')}
            </Text>
          </Badge>
          <Title order={1} fw={900} ta="center">
            {t('homePage.ourTeam.sectionTitle')}
          </Title>
        </Stack>
      </Group>
      <SimpleGrid
        cols={{
          base: 1,
          sm: 2,
          md: 3,
          lg: 5,
        }}
        spacing="lg"
        mb="xl"
      >
        {staticTeamMembers.map((member, index) => (
          <UserBadge
            key={index}
            name={member.name}
            role={member.role}
          />
        ))}
      </SimpleGrid>
    </Container>
  )
}
