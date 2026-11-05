import { useParams, Link } from 'react-router'
import { Anchor, Badge, Container, Divider, Group, Stack, Text, Title } from '@mantine/core'
import { api, useApi, type ResumeDetails } from '@shared/api'
import { formatDate, fullName, period } from './lib/format'
import { PageState } from './ui/PageState'

function Section({ title, text }: { title: string; text: string | null }) {
  if (!text) return null
  return (
    <section>
      <Title order={3} fz={20} mb={8}>
        {title}
      </Title>
      <Text style={{ whiteSpace: 'pre-line' }}>{text}</Text>
    </section>
  )
}

/** Публичная страница резюме — замена `/ru/resumes/:id` старого сайта. */
export function ResumePage() {
  const { id } = useParams()
  const { data: r, error, loading } = useApi(() => api<ResumeDetails>(`/api/resumes/${id}`), [id])

  if (!r) return <PageState loading={loading} error={error} />

  const links = [
    r.githubUrl && { label: 'GitHub', href: r.githubUrl },
    r.hexletUrl && { label: 'Профиль на Хекслете', href: r.hexletUrl },
    r.contactTelegram && {
      label: `Telegram ${r.contactTelegram}`,
      href: `https://t.me/${r.contactTelegram.replace(/^@/, '')}`,
    },
  ].filter((l): l is { label: string; href: string } => Boolean(l))

  return (
    <Container size="md" py={48}>
      <Anchor component={Link} to="/resumes" fz="sm" c="dimmed">
        ← Все резюме
      </Anchor>
      <Title order={1} fz={36} mt="sm">
        {r.name}
      </Title>
      <Group gap="xs" mt="xs">
        <Anchor component={Link} to={`/users/${r.author.id}`} fw={600}>
          {fullName(r.author)}
        </Anchor>
        {r.city && <Badge variant="light">{r.city}</Badge>}
        {r.relocation && <Badge variant="light" color="gray">Переезд: {r.relocation}</Badge>}
        {r.englishFluency && <Badge variant="light" color="gray">Английский: {r.englishFluency}</Badge>}
      </Group>
      {links.length > 0 && (
        <Group gap="md" mt="md">
          {links.map((l) => (
            <Anchor key={l.href} href={l.href} target="_blank" rel="noopener noreferrer nofollow">
              {l.label}
            </Anchor>
          ))}
        </Group>
      )}

      <Divider my="xl" />

      <Stack gap="xl">
        <Section title="О себе" text={r.summary} />
        <Section title="Навыки" text={r.skillsDescription} />

        {r.works.length > 0 && (
          <section>
            <Title order={3} fz={20} mb={8}>
              Опыт работы
            </Title>
            <Stack gap="md">
              {r.works.map((w, i) => (
                <div key={i}>
                  <Text fw={700}>
                    {[w.position, w.company].filter(Boolean).join(' · ')}
                  </Text>
                  <Text fz="sm" c="dimmed">
                    {period(w.beginDate, w.endDate, w.current)}
                  </Text>
                  {w.description && (
                    <Text mt={4} style={{ whiteSpace: 'pre-line' }}>
                      {w.description}
                    </Text>
                  )}
                </div>
              ))}
            </Stack>
          </section>
        )}

        <Section title="Проекты" text={r.projectsDescription} />

        {r.educations.length > 0 && (
          <section>
            <Title order={3} fz={20} mb={8}>
              Образование
            </Title>
            <Stack gap="md">
              {r.educations.map((e, i) => (
                <div key={i}>
                  <Text fw={700}>{[e.institution, e.faculty].filter(Boolean).join(', ')}</Text>
                  <Text fz="sm" c="dimmed">
                    {period(e.beginDate, e.endDate, e.current)}
                  </Text>
                </div>
              ))}
            </Stack>
          </section>
        )}

        <Section title="Достижения" text={r.awardsDescription} />
        <Section title="Дополнительно" text={r.aboutMyself} />
      </Stack>

      {r.updatedAt && (
        <Text fz="xs" c="dimmed" mt={48}>
          Обновлено {formatDate(r.updatedAt)}
        </Text>
      )}
    </Container>
  )
}
