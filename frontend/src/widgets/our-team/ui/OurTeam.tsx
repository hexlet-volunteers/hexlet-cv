import {
  Container,
  Title,
  SimpleGrid,
  Group,
  Stack,
  Badge,
  Paper,
  Avatar,
  Text
} from '@mantine/core'
// import { UserBadge } from './components/UserBadge'
import { useTranslation } from 'react-i18next'
import type { OurTeamCardDto } from '../types'

type TProps = {
  ourTeam: OurTeamCardDto[]
}

export const OurTeam: React.FC<TProps> = (props) => {
  const { ourTeam } = props
  const { t } = useTranslation()

  const renderItem = ({ name, role, src }: OurTeamCardDto, index: number) => {
    return (
      <Paper key={index} radius="lg" p="xs">
        <Avatar src={src} size={120} radius={120} mx="auto" />
        <Text ta="center" fz="lg" fw={500} mt="md">
          {name}
        </Text>
        <Text ta="center" fz="sm">
          {role}
        </Text>
      </Paper>
    )
  }

  return (
    <Container size="lg" py="xs">
      <Group justify="center" mb="xs">
        <Stack gap="xs" align="center">
          <Badge fz="xs" fw="normal" size="lg" variant="light" color="teal" tt="none">
            {t('homePage.ourTeam.aboutBadge')}
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
        {ourTeam.map(renderItem)}
      </SimpleGrid>
    </Container>
  )
}
