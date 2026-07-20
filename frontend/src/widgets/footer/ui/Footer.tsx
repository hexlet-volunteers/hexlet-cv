import {
  Box,
  Text,
  Container,
  Anchor,
  Stack,
  Divider,
  Group,
  Grid,
} from '@mantine/core'
import { Logo } from '@shared/ui/Logo'
import { Link } from 'react-router'
import { FooterNavigation } from './FooterNavigation'
import { FooterSocialLinks } from './FooterSocialLinks'

export function Footer() {
  return (
    <Box component="footer" pt={52} bg="rgb(23, 23, 30)">
      <Container size="lg" px={24}>
        <Grid
          component="nav"
          aria-label="Навигация в футере"
          gutter={36}
          pb={40}
          columns={60}
        >
          <Grid.Col span={{ base: 60, sm: 30, md: 18 }}>
            <Stack>
              <Anchor component={Link} to="/" underline="never">
                <Logo variant="light" />
              </Anchor>
              <Text display="inline-block" w={240} fz={12.5} c="gray.6">
                Сервис Хекслета для поиска работы в IT: от первого резюме до
                подписанного оффера.
              </Text>
              <FooterSocialLinks />
            </Stack>
          </Grid.Col>

          <FooterNavigation />
        </Grid>

        <Divider color="rgba(255, 255, 255, 0.09)" />

        <Group justify="flex-start" align="center" pt="md" pb="lg">
          <Text c="gray.6" size="xs">
            © 2012–2026 Хекслет · Программирование — это просто
          </Text>
        </Group>
      </Container>
    </Box>
  )
}
