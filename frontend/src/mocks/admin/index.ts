import type { KnowledgeBaseEntry } from '@widgets/knowledge-base'
import type { StudyProgramsEntry } from '@widgets/admin-study-programs'
import type { AdminMenuDTO } from '@pages/Admin/components/AdminNavbar'
import { defineGet } from '@mocks/msw/define'
import type { InterviewsEntry } from '@widgets/admin-interviews'
import type { MswCtx } from '@mocks/msw/createCtx'
import type {
  MarketingArticlesDTO,
  MarketingPricingDTO,
  MarketingReviewsDTO,
  MarketingStoriesDTO,
  MarketingTeamDTO,
} from '@widgets/admin-marketing'

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
  {
    id: 2,
    name: 'QA-инженер',
    duration: 4,
    lessons: 32,
    isPublished: false,
  },
]

const baseProps = (ctx: MswCtx) => ({
  flash: {},
  errors: {},
  auth: { user: ctx.user },
  adminMenu: mockMenu,
})

const mockMarketingArticles: MarketingArticlesDTO[] = [
  {
    id: 1,
    title: 'Новая статья о программировании',
    content: 'Полное содержание статьи...',
    imageUrl: 'https://example.com/image.jpg',
    author: 'Иван Иванов',
    readingTime: 5,
    isPublished: true,
    homeComponentId: 'comp_123',
    showOnHomepage: true,
    displayOrder: 1,
    publishedAt: '2024-01-15T10:30:00',
    createdAt: '2024-01-10T12:00:00',
    updatedAt: '2024-01-12T14:30:00',
  },
  {
    id: 2,
    title: 'Статья о JavaScript',
    content: 'Полное содержание статьи...',
    imageUrl: 'https://example.com/image2.jpg',
    author: 'Андрей Петров',
    readingTime: 7,
    isPublished: false,
    homeComponentId: null,
    showOnHomepage: true,
    displayOrder: 2,
    publishedAt: null,
    createdAt: '2024-01-11T12:00:00',
    updatedAt: '2024-01-13T14:30:00',
  },
]

const mockMarketingStories: MarketingStoriesDTO[] = [
  {
    id: 1,
    title: 'История успеха выпускника',
    content: 'Подробная история успеха...',
    imageUrl: 'https://example.com/story-image.jpg',
    isPublished: false,
    showOnHomepage: true,
    displayOrder: 2,
    publishedAt: null,
    createdAt: '2024-01-10T12:00:00',
    updatedAt: '2024-01-12T14:30:00',
  },
  {
    id: 2,
    title: 'История успеха выпускника 2',
    content: 'Подробная история успеха...',
    imageUrl: 'https://example.com/story-image2.jpg',
    isPublished: true,
    showOnHomepage: true,
    displayOrder: 1,
    publishedAt: '2024-01-15T10:30:00',
    createdAt: '2024-01-09T12:00:00',
    updatedAt: '2024-01-11T14:30:00',
  },
]

const mockMarketingReviews: MarketingReviewsDTO[] = [
  {
    id: 1,
    author: 'Иван Иванов',
    content: 'Отличная статья, рекомендую!',
    avatarUrl: 'https://example.com/avatar1.jpg',
    isPublished: true,
    showOnHomepage: true,
    displayOrder: 1,
    publishedAt: '2024-01-15T10:30:00',
    createdAt: '2024-01-10T12:00:00',
    updatedAt: '2024-01-12T14:30:00',
  },
  {
    id: 2,
    author: 'Андрей Петров',
    content: 'Полезная статья, спасибо!',
    avatarUrl: 'https://example.com/avatar2.jpg',
    isPublished: false,
    showOnHomepage: true,
    displayOrder: 2,
    publishedAt: null,
    createdAt: '2024-01-11T12:00:00',
    updatedAt: '2024-01-13T14:30:00',
  },
]

const mockMarketingTeam: MarketingTeamDTO[] = [
  {
    id: 1,
    firstName: 'Иван',
    lastName: 'Иванов',
    position: 'Автор статей',
    memberType: 'Маркетолог',
    avatarUrl: 'https://example.com/avatar1.jpg',
    isPublished: true,
    showOnHomepage: true,
    displayOrder: 1,
    publishedAt: '2024-01-15T10:30:00',
    createdAt: '2024-01-10T12:00:00',
    updatedAt: '2024-01-12T14:30:00',
  },
  {
    id: 2,
    firstName: 'Андрей',
    lastName: 'Петров',
    position: 'Автор историй успеха',
    memberType: 'Маркетолог',
    avatarUrl: 'https://example.com/avatar2.jpg',
    isPublished: false,
    showOnHomepage: true,
    displayOrder: 2,
    publishedAt: null,
    createdAt: '2024-01-11T12:00:00',
    updatedAt: '2024-01-13T14:30:00',
  },
]

const mockMarketingPricing: MarketingPricingDTO[] = [
  {
    id: 1,
    name: 'Базовый план',
    originalPrice: 1000,
    discountPercent: 20,
    finalPrice: 800,
    description: 'Доступ к базовым функциям платформы',
    discountAmount: 200,
    savings: 200,
    hasDiscount: true,
    isFree: false,
    createdAt: '2024-01-10T12:00:00',
    updatedAt: '2024-01-12T14:30:00',
  },
  {
    id: 2,
    name: 'Премиум план',
    originalPrice: 2000,
    discountPercent: 30,
    finalPrice: 1400,
    description: 'Доступ ко всем функциям платформы и приоритетная поддержка',
    discountAmount: 600,
    savings: 600,
    hasDiscount: true,
    isFree: false,
    createdAt: '2024-01-11T12:00:00',
    updatedAt: '2024-01-13T14:30:00',
  },
]

type MarketingSection = keyof typeof marketingSections

const marketingSections = {
  default: {
    data: mockMarketingArticles,
    component: 'Admin/Marketing/Articles/Index',
  },
  articles: {
    data: mockMarketingArticles,
    component: 'Admin/Marketing/Articles/Index',
  },
  stories: {
    data: mockMarketingStories,
    component: 'Admin/Marketing/Stories/Index',
  },
  reviews: {
    data: mockMarketingReviews,
    component: 'Admin/Marketing/Reviews/Index',
  },
  team: { data: mockMarketingTeam, component: 'Admin/Marketing/Team/Index' },
  pricing: {
    data: mockMarketingPricing,
    component: 'Admin/Marketing/Pricing/Index',
  },
}

const marketingHandler = (section: MarketingSection) => {
  return async ({ request }: { request: Request }) => {
    console.log('MSW: handler hit', request.url)

    await delay()

    const config = marketingSections[section]

    return inertiaJson({
      component: config.component,
      props: {
        [section]: config.data,
        adminMenu: mockMenu,
      },
      url: new URL(request.url).pathname,
      version: 'msw-dev',
    })
  }
}

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
  // MARKETING
  http.get('*/admin/marketing', marketingHandler('articles')),
  http.get('*/admin/marketing/articles', marketingHandler('articles')),
  http.get('*/admin/marketing/stories', marketingHandler('stories')),
  http.get('*/admin/marketing/reviews', marketingHandler('reviews')),
  http.get('*/admin/marketing/team', marketingHandler('team')),
  http.get('*/admin/marketing/pricing', marketingHandler('pricing')),
]
