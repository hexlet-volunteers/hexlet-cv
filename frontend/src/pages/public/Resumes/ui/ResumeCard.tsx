import { Anchor, Card, Group, Text } from '@mantine/core'
import { Link } from 'react-router'
import type { ResumeSummary } from '@shared/api'
import { formatDate, fullName } from '../lib/format'

export function ResumeCard({ resume, showAuthor = true }: { resume: ResumeSummary; showAuthor?: boolean }) {
  const meta = [resume.city, resume.relocation && `переезд: ${resume.relocation}`, resume.englishFluency && `английский: ${resume.englishFluency}`]
    .filter(Boolean)
    .join(' · ')

  return (
    <Card withBorder radius="md" padding="lg">
      <Anchor component={Link} to={`/resumes/${resume.id}`} fw={700} fz="lg" c="dark" underline="hover">
        {resume.name}
      </Anchor>
      <Group gap={6} mt={4}>
        {showAuthor && (
          <Anchor component={Link} to={`/profiles/${resume.author.id}`} fz="sm" c="dimmed">
            {fullName(resume.author)}
          </Anchor>
        )}
        {meta && (
          <Text fz="sm" c="dimmed">
            {showAuthor ? '· ' : ''}
            {meta}
          </Text>
        )}
      </Group>
      {resume.summary && (
        <Text fz="sm" mt="sm" lineClamp={3}>
          {resume.summary}
        </Text>
      )}
      {resume.updatedAt && (
        <Text fz="xs" c="dimmed" mt="sm">
          Обновлено {formatDate(resume.updatedAt)}
        </Text>
      )}
    </Card>
  )
}
