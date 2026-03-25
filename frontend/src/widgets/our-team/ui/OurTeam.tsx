import {
  Container,
  Title,
  SimpleGrid,
  Group,
  Stack,
  Badge,
  Paper,
  Avatar,
  Text,
  useMantineTheme,
} from '@mantine/core'
import { useTranslation } from 'react-i18next'
import type { OurTeamCardDto } from '../types'

/**
 * Массив данных об участниках команды.
 * Если данные отсутствуют или массив пустой, секция не отображается.
 */
type TProps = {
  ourTeam?: OurTeamCardDto[] | null
}

/**
 * Виджет "Наша команда" для главной страницы.
 * Отображает сетку карточек участников команды.
 *
 * @remarks
 * Виджет скрывается полностью, если:
 * - Данные не переданы (undefined/null)
 * - Передан пустой массив
 *
 * @param props - список участников команды
 * @returns React-компонент или null, если нет данных
 */
export const OurTeam: React.FC<TProps> = (props) => {
  const theme = useMantineTheme()
  const { ourTeam } = props
  const { t } = useTranslation()

  if (!ourTeam || ourTeam.length === 0) {
    return null
  }

  /**
   * Отрисовывает карточку отдельного участника команды.
   *
   * @param item - Данные участника команды {@link OurTeamCardDto}
   * @returns JSX элемент карточки с аватаром, именем и ролью
   *
   * @remarks
   * В качестве key используется уникальный идентификатор `item.id`.
   * Карточка содержит аватар, имя участника и его должность.
   */
  const renderItem = (item: OurTeamCardDto) => {
    const { id, name, role, src } = item

    return (
      <Paper key={id} radius="lg" p="xs">
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
          <Badge
            fz="xs"
            fw="normal"
            size="lg"
            variant="light"
            color={theme.primaryColor}
            tt="none"
          >
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