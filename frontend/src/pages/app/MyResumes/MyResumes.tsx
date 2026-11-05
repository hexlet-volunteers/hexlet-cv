import { Link } from 'react-router'
import {
  Alert,
  Anchor,
  Badge,
  Button,
  Card,
  Center,
  Group,
  Loader,
  Stack,
  Text,
  Title,
} from '@mantine/core'
import { IconPlus } from '@tabler/icons-react'
import { RequireAuth } from '@features/auth'
import { api, useApi, type OwnResume } from '@shared/api'

function List() {
  const { data, error, loading } = useApi(
    () => api<OwnResume[]>('/api/account/resumes'),
    [],
  )

  if (loading) {
    return (
      <Center py={80}>
        <Loader />
      </Center>
    )
  }
  if (error || !data) {
    return (
      <Alert color="red">
        Не удалось загрузить резюме. Попробуйте обновить страницу.
      </Alert>
    )
  }
  if (data.length === 0) {
    return (
      <Card withBorder radius="md" p="xl">
        <Text fw={700} fz="lg">
          У вас пока нет резюме
        </Text>
        <Text c="dimmed" mt={4} mb="md">
          Заполните его за несколько минут и опубликуйте — оно появится в
          каталоге.
        </Text>
        <Button
          component={Link}
          to="/app/resumes/new"
          leftSection={<IconPlus size={16} />}
          w="fit-content"
        >
          Создать резюме
        </Button>
      </Card>
    )
  }

  return (
    <Stack gap="sm">
      {data.map((r) => (
        <Card key={r.id} withBorder radius="md" padding="lg">
          <Group justify="space-between" wrap="nowrap">
            <div>
              <Anchor
                component={Link}
                to={`/app/resumes/${r.id}`}
                fw={700}
                fz="lg"
                c="dark"
              >
                {r.name}
              </Anchor>
              <Group gap="xs" mt={4}>
                <Badge color={r.published ? 'green' : 'gray'} variant="light">
                  {r.published ? 'Опубликовано' : 'Черновик'}
                </Badge>
                {r.city && (
                  <Text fz="sm" c="dimmed">
                    {r.city}
                  </Text>
                )}
              </Group>
            </div>
            <Group gap="xs" wrap="nowrap">
              {r.published && (
                <Button
                  component={Link}
                  to={`/resumes/${r.id}`}
                  variant="subtle"
                  size="xs"
                >
                  Открыть
                </Button>
              )}
              <Button
                component={Link}
                to={`/app/resumes/${r.id}`}
                variant="light"
                size="xs"
              >
                Редактировать
              </Button>
            </Group>
          </Group>
        </Card>
      ))}
    </Stack>
  )
}

/** Мои резюме: список с черновиками и опубликованными (#1309). */
export function MyResumes() {
  return (
    <RequireAuth>
      <Stack maw={860}>
        <Group justify="space-between">
          <Title order={1} fz={28}>
            Мои резюме
          </Title>
          <Button
            component={Link}
            to="/app/resumes/new"
            leftSection={<IconPlus size={16} />}
          >
            Новое резюме
          </Button>
        </Group>
        <List />
      </Stack>
    </RequireAuth>
  )
}
