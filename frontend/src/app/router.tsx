import { createBrowserRouter } from 'react-router'
import { PublicLayout, AppLayout, AdminLayout } from '@widgets/layout'
import { Landing } from '@pages/public/Landing'
import { Login } from '@pages/public/Login'
import { Register } from '@pages/public/Register'
import { NotFound } from '@pages/public/NotFound'
import { Overview } from '@pages/app/Overview'
import { Vacancies } from '@pages/app/Vacancies'
import { Tracker } from '@pages/app/Tracker'
import { Resume } from '@pages/app/Resume'
import { ScreenStub } from '@shared/ui/ScreenStub'

const stub = (title: string, description: string, issue: string) => (
  <ScreenStub title={title} description={description} issue={issue} />
)

export const router = createBrowserRouter([
  {
    path: '/',
    element: <PublicLayout />,
    children: [
      { index: true, element: <Landing /> },
      { path: 'login', element: <Login /> },
      { path: 'register', element: <Register /> },
      {
        path: 'legal',
        element: stub(
          'Правовая информация',
          'Разделы: общие положения, персональные данные, работа AI-функций, подписка и оплата, ответственность.',
          '1140',
        ),
      },
      {
        path: 'blog',
        element: stub(
          'Блог о поиске работы',
          'Список статей: данные выпускников, тактика откликов и фоллоу-апов.',
          '1199',
        ),
      },
      {
        path: 'blog/:id',
        element: stub(
          'Статья блога',
          'Страница отдельной статьи блога.',
          '1200',
        ),
      },
      {
        path: '*',
        element: <NotFound />,
      },
    ],
  },
  {
    path: '/app',
    element: <AppLayout />,
    children: [
      { index: true, element: <Overview /> },
      { path: 'vacancies', element: <Vacancies /> },
      { path: 'tracker', element: <Tracker /> },
      { path: 'resume', element: <Resume /> },
      {
        path: 'onboarding',
        element: stub(
          'Онбординг',
          'Мультистеп-настройка поиска: роль, уровень, формат, портфолио.',
          '1142',
        ),
      },
      {
        path: 'salaries',
        element: stub(
          'Зарплатный радар',
          'Перцентили, грейды, города; фильтры по уровню и локации.',
          '1184',
        ),
      },
      {
        path: 'referrals',
        element: stub(
          'Рефералы',
          'Каталог компаний выпускников и запрос рекомендации.',
          '1187',
        ),
      },
      {
        path: 'analytics',
        element: stub(
          'Аналитика',
          'Воронка, конверсия, разбор отказов с AI.',
          '1181',
        ),
      },
      {
        path: 'letters',
        element: stub(
          'Письма',
          '6 AI-шаблонов: сопроводительное, фоллоу-ап, благодарственное, рекрутёру, реферальное, торг.',
          '1169',
        ),
      },
      {
        path: 'profile',
        element: stub(
          'Профиль',
          'Публичный профиль, параметры поиска, интеграции, приватность.',
          '1190',
        ),
      },
      {
        path: 'ai-interview',
        element: stub(
          'AI-собес',
          'Сессия с прогоном кода реальными тестами и отчётом.',
          '1172',
        ),
      },
      {
        path: 'mocks',
        element: stub(
          'Живые моки',
          'Бронирование мока с наставником, комната, записи, фидбэк.',
          '1174',
        ),
      },
      {
        path: 'questions',
        element: stub(
          'База вопросов',
          'Поиск вопросов и сборка тренировки.',
          '1178',
        ),
      },
      {
        path: 'recordings',
        element: stub(
          'Записи собесов',
          'Записи с метаданными и отчётами.',
          '1176',
        ),
      },
      {
        path: 'commercial-projects',
        element: stub(
          'Комм. проекты',
          'Заявка на участие в коммерческом проекте (раздел «Опыт»).',
          '1203',
        ),
      },
      {
        path: 'settings',
        element: stub(
          'Настройки',
          'Уведомления, приватность, аккаунт, подписка.',
          '1202',
        ),
      },
    ],
  },
  {
    path: '/admin',
    element: <AdminLayout />,
    children: [
      {
        index: true,
        element: stub('Админ · Дашборд', 'Обзорные метрики платформы.', '1193'),
      },
      {
        path: 'users',
        element: stub(
          'Админ · Пользователи',
          'Список пользователей + экспорт CSV.',
          '923',
        ),
      },
      {
        path: 'blog',
        element: stub('Админ · Блог', 'CRUD статей и черновиков.', '1194'),
      },
      {
        path: 'mocks',
        element: stub(
          'Админ · Живые моки',
          'Наставники, слоты, приглашения.',
          '1196',
        ),
      },
      {
        path: 'projects',
        element: stub(
          'Админ · Заявки на комм. проекты',
          'Заявки студентов и управление проектами.',
          '1195',
        ),
      },
      {
        path: 'sources',
        element: stub(
          'Админ · Источники и парсеры',
          'Управление источниками вакансий.',
          '1197',
        ),
      },
    ],
  },
])
