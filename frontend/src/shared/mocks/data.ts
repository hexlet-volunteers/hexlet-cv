// Мок-данные MVP «Хекслет Карьера». Заменяются сгенерированным API-клиентом (Design First, #1106).

export interface User {
  name: string
  role: string
  status: string
  initials: string
}

export const user: User = {
  name: 'Артём Крылов',
  role: 'Frontend · React',
  status: 'выпускник',
  initials: 'АК',
}

export interface StatCard {
  label: string
  value: string
  note: string
  progress?: number
}

export const overviewStats: StatCard[] = [
  {
    label: 'Отклики на этой неделе',
    value: '12 из 15',
    note: '80% недельной цели',
    progress: 80,
  },
  {
    label: 'Ответы работодателей',
    value: '4',
    note: '33% · выше медианы выпускников',
  },
  { label: 'Собеседования', value: '2', note: 'Т-Банк — завтра, 11:00' },
  { label: 'Резюме v3', value: '78/100', note: '2 правки до 85+' },
]

export interface TodayTask {
  title: string
  detail: string
  badge: string
  action: string
}

export const todayTasks: TodayTask[] = [
  {
    title: 'Фоллоу-ап в Контур',
    detail: '3 дня тишины после скрининга — пора вежливо напомнить',
    badge: 'пора',
    action: 'Написать',
  },
  {
    title: 'Подготовка к собесу с Т-Банком',
    detail: 'Тренировка «Техническое · React» — прошлый балл 72',
    badge: 'завтра 11:00',
    action: 'Начать',
  },
  {
    title: 'Добавить метрики в блок «Опыт»',
    detail: 'Совет AI-ревью: цифры поднимут резюме с 78 до ~85',
    badge: 'AI',
    action: 'К резюме',
  },
]

export interface FunnelStage {
  label: string
  count: number
}

export const funnel: FunnelStage[] = [
  { label: 'Отклики', count: 47 },
  { label: 'Ответы', count: 14 },
  { label: 'Скрининги', count: 8 },
  { label: 'Собесы', count: 5 },
  { label: 'Офферы', count: 1 },
]

export interface Vacancy {
  id: string
  title: string
  company: string
  location: string
  salary: string
  source: string
  posted: string
  stack: string[]
  match: number
  isNew?: boolean
}

export const vacancies: Vacancy[] = [
  {
    id: 'v1',
    title: 'Junior Frontend (React)',
    company: 'Авито',
    location: 'Удалённо',
    salary: '120–160 тыс ₽',
    source: 'hh.ru',
    posted: 'сегодня',
    stack: ['React', 'TypeScript', 'Redux'],
    match: 92,
    isNew: true,
  },
  {
    id: 'v2',
    title: 'Frontend-разработчик',
    company: 'Т-Банк',
    location: 'Москва, гибрид',
    salary: 'от 140 тыс ₽',
    source: 'Хабр Карьера',
    posted: 'вчера',
    stack: ['React', 'TypeScript', 'Jest'],
    match: 87,
    isNew: true,
  },
  {
    id: 'v3',
    title: 'Junior React Developer',
    company: 'Selectel',
    location: 'СПб, офис',
    salary: '100–130 тыс ₽',
    source: 'Getmatch',
    posted: '2 дня назад',
    stack: ['React', 'JavaScript'],
    match: 84,
  },
  {
    id: 'v4',
    title: 'Frontend (джуниор+)',
    company: 'МТС Диджитал',
    location: 'Удалённо',
    salary: '130–170 тыс ₽',
    source: 'Telegram',
    posted: '3 дня назад',
    stack: ['React', 'TypeScript', 'MobX'],
    match: 79,
  },
]

export interface Application {
  id: string
  company: string
  title: string
  resume: string
  note: string
}

export interface TrackerColumn {
  id: string
  title: string
  color: string
  items: Application[]
}

export const trackerColumns: TrackerColumn[] = [
  {
    id: 'saved',
    title: 'Сохранено',
    color: 'gray',
    items: [
      {
        id: 'a1',
        company: 'Точка',
        title: 'Junior Frontend',
        resume: 'v3',
        note: '110–140 тыс ₽ · добавлено 2 дня назад',
      },
      {
        id: 'a2',
        company: 'Lamoda Tech',
        title: 'Junior FE Developer',
        resume: 'v3',
        note: 'не указана · добавлено вчера',
      },
    ],
  },
  {
    id: 'sent',
    title: 'Отклик отправлен',
    color: 'blue',
    items: [
      {
        id: 'a3',
        company: 'Ozon Tech',
        title: 'Junior Frontend',
        resume: 'v3',
        note: '+ сопроводительное · отправлен 2 дня назад',
      },
      {
        id: 'a4',
        company: 'МТС Диджитал',
        title: 'Frontend (джуниор+)',
        resume: 'v3',
        note: '7 дней тишины — фоллоу-ап?',
      },
    ],
  },
  {
    id: 'screening',
    title: 'Скрининг',
    color: 'yellow',
    items: [
      {
        id: 'a5',
        company: 'Контур',
        title: 'Junior Frontend',
        resume: 'v3',
        note: 'обещали ответ — 3 дня тишины',
      },
    ],
  },
  {
    id: 'interview',
    title: 'Собеседование',
    color: 'violet',
    items: [
      {
        id: 'a6',
        company: 'Т-Банк',
        title: 'Frontend-разработчик',
        resume: 'v3',
        note: 'завтра 11:00 · техническое',
      },
    ],
  },
  {
    id: 'offer',
    title: 'Оффер',
    color: 'green',
    items: [
      {
        id: 'a7',
        company: 'Эвотор',
        title: 'Junior Frontend',
        resume: 'v3',
        note: '155 000 ₽ · ответить до 8 июля',
      },
    ],
  },
]

export interface ResumeVersion {
  version: string
  label: string
  score: number
  hint: string
}

export const resumeVersions: ResumeVersion[] = [
  {
    version: 'v3',
    label: 'Продуктовые компании',
    score: 78,
    hint: '2 правки до 85+',
  },
  { version: 'v2', label: 'Стартапы', score: 71, hint: 'устарела' },
]

export const resumeSkills: string[] = [
  'JavaScript',
  'TypeScript',
  'React',
  'Redux Toolkit',
  'Jest',
  'Git',
  'REST',
]
