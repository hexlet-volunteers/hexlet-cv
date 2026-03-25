import { http, delay } from 'msw'
import { inertiaJson } from '../inertia'
import type { InterviewsEntry } from '@widgets/admin-interviews/ui/AdminInterviews'
import type { KnowledgeBaseEntry } from '@widgets/knowledge-base'
import type { StudyProgramsEntry } from '@widgets/admin-study-programs'
import type { AdminMenuDTO } from '@pages/Admin/components/AdminNavbar'

const mockMenu: AdminMenuDTO[] = [
  {
    category: 'КОНТЕНТ',
    items: [
      { label: 'Маркетинг', link: '/admin/marketing', icon: 'IconSpeakerphone' },
      { label: 'Вебинары', link: '/admin/webinars', icon: 'IconVideo' },
      { label: 'База знаний', link: '/admin/knowledgebase', icon: 'IconBooks' },
      { label: 'Интервью', link: '/admin/interview', icon: 'IconMicrophone' },
      { label: 'Грейдирование', link: '/admin/grading', icon: 'IconStar' },
      { label: 'Программы обучения', link: '/admin/programs', icon: 'IconSchool' },
    ],
  },
  {
    category: 'АДМИНИСТРИРОВАНИЕ',
    items: [
      { label: 'Пользователи', link: '/admin/users', icon: 'IconUsers' },
      { label: 'Настройки', link: '/admin/settings', icon: 'IconSettings' },
    ],
  },
  {
    category: 'ПОМОЩЬ',
    items: [
      { label: 'Помощь', link: '/admin/help', icon: 'IconHelp' },
    ],
  },
]

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
]
