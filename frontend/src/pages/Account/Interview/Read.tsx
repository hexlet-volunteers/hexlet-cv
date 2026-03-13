import { Stack, Text, Title } from '@mantine/core'
import type { InertiaPage } from '@shared/types/inertia'
import type { KnowledgeInterviewShowResponseDTO } from '@entities/interview'
import { AppLayout } from '@pages/Account/components/AppLayout'

const InterviewReadPage: InertiaPage<KnowledgeInterviewShowResponseDTO> = ({
  interview,
}) => {
  return (
    <Stack>
      <Title order={2}>{interview.title}</Title>
      {interview.description && <Text>{interview.description}</Text>}
    </Stack>
  )
}

InterviewReadPage.layout = page => <AppLayout>{page}</AppLayout>

export default InterviewReadPage
