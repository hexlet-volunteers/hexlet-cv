import type { WebinarDTO } from '@widgets/admin-webinars'
import type { KnowledgeBaseEntry } from '@widgets/knowledge-base'
import type { StudyProgramsEntry } from '@widgets/admin-study-programs'
import type { AdminMenuDTO } from '@pages/Admin/components/AdminNavbar'
import { defineGet } from '@mocks/msw/define'
import type { InterviewsEntry } from '@widgets/admin-interviews'
import type { MswCtx } from '@mocks/msw/createCtx'

const mockMenu: AdminMenuDTO[] = [
  {
    category: 'КОНТЕНТ',
    items: [
      {
        label: 'Маркетинг',
        link: '/admin/marketing',
        icon: 'IconSpeakerphone',
      },
      { label: 'Вебинары', link: '/admin/webinars', icon: 'IconVideo' },
      { label: 'База знаний', link: '/admin/knowledgebase', icon: 'IconBooks' },
      { label: 'Интервью', link: '/admin/interview', icon: 'IconMicrophone' },
      { label: 'Грейдирование', link: '/admin/grading', icon: 'IconStar' },
      {
        label: 'Программы обучения',
        link: '/admin/programs',
        icon: 'IconSchool',
      },
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
    items: [{ label: 'Помощь', link: '/admin/help', icon: 'IconHelp' }],
  },
]

const mockWebinars: WebinarDTO[] = [
  { id: 1, name: 'Онбординг в LMS', date: '2025-10-01', registration: 'https://example.com/reg1', videoUrl: '', feature: true, isPublished: true },
  { id: 2, name: 'Метрики e-learning', date: '2025-10-15', registration: 'https://example.com/reg2', videoUrl: '', feature: false, isPublished: false }
]

const mockInterviews: InterviewsEntry[] = [
  {
    id: 1,
    title: 'Интервью с продактом',
    speaker: 'Алексей С.',
    videoUrl: '',
    isPublished: true,
  },
  {
    id: 2,
    title: 'Интервью: редактор',
    speaker: 'Наталья О.',
    videoUrl: '',
    isPublished: false,
  },
]

const mockArticles: KnowledgeBaseEntry[] = [
  { id: 1, title: 'FAQ по платформе', category: 'Общая', isPublished: true },
  {
    id: 2,
    title: 'Глоссарий терминов',
    category: 'Справка',
    isPublished: false,
  },
]

const mockPrograms: StudyProgramsEntry[] = [
  {
    id: 1,
    name: 'Frontend-разработчик',
    duration: 6,
    lessons: 48,
    isPublished: true,
  },
  { id: 2, name: 'QA-инженер', duration: 4, lessons: 32, isPublished: false },
]

const baseProps = (ctx: MswCtx) => ({
  flash: {},
  errors: {},
  auth: { user: ctx.user },
  adminMenu: mockMenu,
})

export const adminHandlers = [
  defineGet('*/admin', (ctx) => {
    return ctx.inertiaPage(
      'Admin/Interview/Index',
      {
        ...baseProps(ctx),
        interviews: mockInterviews,
      },
      200,
      `/${ctx.locale}/admin/interview`,
    )
  }),
  defineGet('*/admin/webinars', (ctx) => {
    return ctx.inertiaPage(
      'Admin/Webinars/Index',
      {
        ...baseProps(ctx),
        webinars: mockWebinars,
      },
      200,
      `/${ctx.locale}/admin/webinars`,
    )
  }),
  defineGet('*/admin/interview', (ctx) => {
    return ctx.inertiaPage(
      'Admin/Interview/Index',
      {
        ...baseProps(ctx),
        interviews: mockInterviews,
      },
      200,
      `/${ctx.locale}/admin/interview`,
    )
  }),
  defineGet('*/admin/knowledgebase', (ctx) => {
    return ctx.inertiaPage(
      'Admin/Knowledgebase/Index',
      {
        ...baseProps(ctx),
        articles: mockArticles,
      },
      200,
      `/${ctx.locale}/admin/knowledgebase`,
    )
  }),
  defineGet('*/admin/programs', (ctx) => {
    return ctx.inertiaPage(
      'Admin/Programs/Index',
      {
        ...baseProps(ctx),
        programs: mockPrograms,
      },
      200,
      `/${ctx.locale}/admin/programs`,
    )
  }),
]
