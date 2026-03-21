import { http, delay } from 'msw'
import { inertiaJson } from '@mocks/inertia'
import type { InterviewsEntry } from '@widgets/admin-interviews/ui/AdminInterviews'
import type { KnowledgeBaseEntry } from '@widgets/knowledge-base'
import type { StudyProgramsEntry } from '@widgets/admin-study-programs'
import { usersHandlers } from '@mocks/admin/users'
import { mockMenu } from '@mocks/admin/consts'

const mockInterviews: InterviewsEntry[] = [
  { id: 1, title: 'Интервью с продактом', speaker: 'Алексей С.', videoUrl: '', isPublished: true },
  { id: 2, title: 'Интервью: редактор', speaker: 'Наталья О.', videoUrl: '', isPublished: false },
]

const mockArticles: KnowledgeBaseEntry[] = [
  { id: 1, title: 'FAQ по платформе', category: 'Общая', isPublished: true },
  { id: 2, title: 'Глоссарий терминов', category: 'Справка', isPublished: false }
]

const mockPrograms: StudyProgramsEntry[] = [
  { id: 1, name: 'Frontend-разработчик', duration: 6, lessons: 48, isPublished: true },
  { id: 2, name: 'QA-инженер', duration: 4, lessons: 32, isPublished: false }
]

export const adminHandlers = [
  http.get('*/admin/interview', async ({ request }) => {
    console.log('MSW: handler hit', request.url)

    await delay()

    return inertiaJson({
      component: 'Admin/Interview/Index',
      props: {
        interviews: mockInterviews,
        adminMenu: mockMenu,
      },
      url: new URL(request.url).pathname,
      version: 'msw-dev',
    })
  }),
  http.get('*/admin/knowledgebase', async ({ request }) => {
    console.log('MSW: handler hit', request.url)

    await delay()

    return inertiaJson({
      component: 'Admin/Knowledgebase/Index',
      props: {
        articles: mockArticles,
        adminMenu: mockMenu,
      },
      url: new URL(request.url).pathname,
      version: 'msw-dev',
    })
  }),
  http.get('*/admin/programs', async ({ request }) => {
    console.log('MSW: handler hit', request.url)

    await delay()

    return inertiaJson({
      component: 'Admin/Programs/Index',
      props: {
        programs: mockPrograms,
        adminMenu: mockMenu,
      },
      url: new URL(request.url).pathname,
      version: 'msw-dev',
    })
  }),
  ...usersHandlers,
]
