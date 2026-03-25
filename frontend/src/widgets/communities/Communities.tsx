import { Badge, Button, Center, Container, SimpleGrid, Stack, Text, Title } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { TelegramLink } from './components/TelegramLink'
import { openTelegramLink } from './helpers/openTelegramInNewTab'

const TG_USERNAME_HEXLET_CAREER_BOT = 'HexletCareerBot'
const TG_USERNAME_HEXLET_RU = 'hexlet_ru'
const TG_USERNAME_HEXLET_JUNIOR_VACANCIES = 'junior_vacancies'
const TG_USERNAME_HEXLET_COMMUNITY = 'hexletcommunity'

const SIMPLE_GRID_COLS_CONFIG = {
  base: 1,
  sm: 2,
  md: 3,
}

export const Communities: React.FC = () => {
  const { t } = useTranslation()

  return (
    <Container size="lg" py="xl">
      <Stack gap="md">
        <Center>
          <Stack gap="md" align="center">
            <Badge
              color="violet"
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
              {t('communities.anchor')}
            </Badge>
            <Title c="white" order={1}>
              {t('communities.title')}
            </Title>
            <Text c="white">{t('communities.description')}</Text>
          </Stack>
        </Center>
        <SimpleGrid cols={SIMPLE_GRID_COLS_CONFIG}>
          <TelegramLink
            text={t('communities.link_bot_description')}
            title={t('communities.link_bot_title')}
            username={TG_USERNAME_HEXLET_CAREER_BOT}
          />
          <TelegramLink
            text={t('communities.link_channel_hexlet_description')}
            title={t('communities.link_channel_hexlet_title')}
            username={TG_USERNAME_HEXLET_RU}
          />
          <TelegramLink
            text={t('communities.link_channel_vacancies_description')}
            title={t('communities.link_channel_vacancies_title')}
            username={TG_USERNAME_HEXLET_JUNIOR_VACANCIES}
          />
        </SimpleGrid>
        <Center>
          <Button
            color="black"
            fz="xs"
            onClick={() => {
              openTelegramLink(TG_USERNAME_HEXLET_COMMUNITY)
            }}
            radius="md"
            variant="white"
          >
            {t('communities.action_connect_to')}
          </Button>
        </Center>
      </Stack>
    </Container>
  )
}
