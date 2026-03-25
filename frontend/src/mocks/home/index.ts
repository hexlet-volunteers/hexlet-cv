import { http, delay } from 'msw'
import type { IArticle } from '@widgets/articles'
import { inertiaJson } from '@mocks/inertia'
import type { PerformanceCardDto } from '@widgets/performance-review'
import type { TrainingCardDto } from '@widgets/training-programs'
import type { OurTeamCardDto } from '@widgets/our-team'

const performanceReview: PerformanceCardDto[] = [
  {
    description: 'Практические задачи, ревью кода и чек‑лист по soft-skills.',
    title: 'Тестирование навыков',
  },
  {
    description: 'Оценка по KPI и вкладу в проекты, плюс развёрнутая обратная связь от менторов.',
    title: 'Перформанс‑ревью',
  },
  {
    description: 'Сопоставление с вилками и требованиями - прозрачный отчёт и шаги роста.',
    title: 'Грейд и рынок',
  },
]

const trainingPrograms: TrainingCardDto[] = [
  {
    description: 'Стратегия поиска, позиционирование, резюме, собеседования.',
    title: 'Как искать работу',
  },
  {
    description: 'Портфолио, бриф, коммуникации, ценообразование, договорённости.',
    title: 'Как работать на фрилансе',
  },
  {
    description: 'Рынки, площадки, подготовка профилей и откликов на английском.',
    title: 'Как искать валютную работу',
  },
]

const articles: IArticle[] = [
  { readingTime: 1,
    tags: ['Про собеседование', 'Создаем резюме'],
    title: 'Сопроводительное письмо и резюме для IT: примеры и советы' },
  { readingTime: 3,
    tags: ['Про автоклики'],
    title: 'Как настроить автоотклики на hh: быстрый поиск работы с ИИ' },
  { readingTime: 5,
    tags: ['Проходим собеседование'],
    title: 'Как пройти собеседование: частые ошибки и вопросы' },
]

const ourTeam: OurTeamCardDto[] = [
  {
    id: 1,
    name: 'Максим',
    role: 'Основатель сервиса',
    src: '',
  },
  {
    id: 2,
    name: 'Альберт',
    role: 'Администратор',
    src: '',
  },
  {
    id: 3,
    name: 'Таня',
    role: 'HR-менеджер',
    src: '',
  },
  {
    id: 4,
    name: 'Слава',
    role: 'Карьерный консультант',
    src: '',
  },
  {
    id: 5,
    name: 'Лера',
    role: 'Карьерный консультант',
    src: '',
  },
]

export const handlers = [
  http.get(/\/(\?.*)?$/, async ({ request }) => {
    console.log('MSW handler hit:', request.method, request.url)

    await delay()

    const page = {
      component: 'Home',
      props: {
        trainingPrograms,
        performanceReview,
        articles,
        ourTeam,
        errors: {},
      },
      url: '/',
      version: 'msw-dev',
    }

    return inertiaJson(page)
  }),
]
