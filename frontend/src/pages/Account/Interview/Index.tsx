import { Container } from '@mantine/core'
import { IconMessage, IconSearchOff } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import type { InertiaPage } from '@shared/types/inertia'
import type { KnowledgeInterviewsResponseDTO } from '@entities/interview'
import { AppLayout } from '@pages/Account/components/AppLayout'
import { PageHeader } from '@widgets/page-header'
import { EntityGrid } from '@widgets/entity-grid'
import { InterviewCard } from '@widgets/interview-card'
import { OpenInterviewButton } from '@features/open-interview'

const InterviewsPage: InertiaPage<KnowledgeInterviewsResponseDTO> = ({
  interviews,
  pagination,
}) => {
  const { t } = useTranslation()

  return (
    <Container fluid>
      <PageHeader
        icon={<IconMessage />}
        title={t('accountPage.interviews.title')}
      />

      <EntityGrid
        data={interviews}
        pagination={pagination}
        baseUrl="/account/interviews"
        emptyConfig={{
          title: t('emptyPlaceholders.noInterviews'),
          icon: IconSearchOff,
          buttonLink: '/account/interviews',
          buttonLabel: t('buttonsLabels.read'),
        }}
        renderItem={interview => (
          <InterviewCard
            interview={interview}
            actionButton={(
              <OpenInterviewButton interviewId={interview.id}>
                {t('buttonsLabels.read')}
              </OpenInterviewButton>
            )}
          />
        )}
      />
    </Container>
  )
}

InterviewsPage.layout = page => <AppLayout>{page}</AppLayout>

export default InterviewsPage
