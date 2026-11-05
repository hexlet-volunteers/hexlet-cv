import { useParams } from 'react-router'
import { Container, Stack, Text, Title } from '@mantine/core'
import { api, useApi, type PublicProfile } from '@shared/api'
import { fullName } from './lib/format'
import { PageState } from './ui/PageState'
import { ResumeCard } from './ui/ResumeCard'

/** Публичный профиль — замена `/ru/users/:id` старого сайта. */
export function UserProfile() {
  const { id } = useParams()
  const { data: p, error, loading } = useApi(() => api<PublicProfile>(`/api/users/${id}`), [id])

  if (!p) return <PageState loading={loading} error={error} />

  return (
    <Container size="md" py={48}>
      <Title order={1} fz={36}>
        {fullName(p)}
      </Title>
      {p.about && (
        <Text mt="sm" style={{ whiteSpace: 'pre-line' }}>
          {p.about}
        </Text>
      )}
      <Title order={2} fz={22} mt={40} mb="md">
        Резюме
      </Title>
      {p.resumes.length > 0 ? (
        <Stack gap="md">
          {p.resumes.map((r) => (
            <ResumeCard key={r.id} resume={r} showAuthor={false} />
          ))}
        </Stack>
      ) : (
        <Text c="dimmed">Опубликованных резюме пока нет.</Text>
      )}
    </Container>
  )
}
