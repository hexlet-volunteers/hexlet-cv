import { Container } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { IconBook, IconShoppingCart } from '@tabler/icons-react'
import type { InertiaPage } from '@shared/types/inertia'
import type { IProgressResponse } from '@entities/learning-progress/model'
// eslint-disable-next-line fsd/forbidden-imports
import { AppLayout } from '@pages/Account/components/AppLayout'
import { EntityGrid } from '@widgets/entity-grid'
import { LearningProgressCard } from '@widgets/learning-progress-card'
import { PageHeader } from '@widgets/page-header'
import { OpenProgramButton } from '@features/open-learning-program'

/**
 * Отображает страницу прогресса обучения с карточками программ пользователя.
 */
const MyProgress: InertiaPage<IProgressResponse> = ({
  progress,
  pagination,
}) => {
  const { t } = useTranslation()

  const sortedProgress = [...progress].sort(
    (a, b) =>
      new Date(b.lastActivityAt).getTime() -
      new Date(a.lastActivityAt).getTime(),
  )

  const activeProgramId = sortedProgress[0]?.id
  const catalogLink = 'https://hexlet.io'

  return (
    <Container fluid>
      <PageHeader
        icon={<IconBook />}
        title={t('accountPage.progress.title')}
        actionButton={
          activeProgramId && (
            <OpenProgramButton variant="outline" programId={activeProgramId}>
              {t('buttonsLabels.continue')}
            </OpenProgramButton>
          )
        }
      />
      <EntityGrid
        data={sortedProgress}
        pagination={pagination}
        baseUrl="/account/my-progress"
        emptyConfig={{
          title: t('emptyPlaceholders.noLearningProgress'),
          icon: IconShoppingCart,
          buttonLink: catalogLink,
          buttonLabel: t('buttonsLabels.goToCatalog'),
        }}
        // Рендер конкретного элемента
        renderItem={(program) => (
          <LearningProgressCard
            key={program.id}
            program={program}
            actionButton={
              <OpenProgramButton programId={program.id}>
                {t('buttonsLabels.open')}
              </OpenProgramButton>
            }
          />
        )}
      />
    </Container>
  )
}

MyProgress.layout = (page) => <AppLayout>{page}</AppLayout>

export default MyProgress
