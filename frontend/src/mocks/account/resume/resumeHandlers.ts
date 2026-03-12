import { http, delay } from 'msw'
import { inertiaJson } from '@mocks/inertia'
import { menu, activityCards } from '../index'

export const resumeHandlers = [
  http.get('/account/resume', async ({ request }) => {
    console.log('MSW: handler hit', request.url)

    await delay()

    return inertiaJson({
      component: 'Account/Resume/Index',
      props: {
        menu,
        activityCards,
        resume: {
          header: 'Аналитик данных - {Имя, Фамилия}',
          summary: '2+ года аналитики. Сильный SQL, Python, BI. KPI-ориентирован',
          skills: 'SQL, Python (pandas, numpy), Tableau/Power BI, A/B-тесты, когортный анализ, прогнозирование',
          experience: '2024–2025 · Проект X: витрины, дашборды, снижение времени на отчётность на 30% 2023–2024 · Проект Y: внедрил продуктовые метрики, рост конверсии на 12%',
        },
      },
      url: '/account/resume',
    })
  }),
]
