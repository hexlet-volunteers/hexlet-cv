import {
  IconBrandTelegram,
  IconBrandVk,
  IconBrandYoutube,
  IconBrandGithub,
} from '@tabler/icons-react'
import { type NavSection, type SocialIcon } from './types'

/**
 * Список поддерживаемых социальных сетей со ссылками на официальные аккаунты.
 * Используется в компоненте `FooterSocialLinks` для рендеринга кнопок-иконок.
 */
export const socialIcons: SocialIcon[] = [
  {
    id: 'tg',
    label: 'Telegram',
    link: 'https://t.me',
    icon: IconBrandTelegram,
  },
  {
    id: 'vk',
    label: 'ВКонтакте',
    link: 'https://vk.com',
    icon: IconBrandVk,
  },
  {
    id: 'yt',
    label: 'YouTube',
    link: 'https://youtube.com',
    icon: IconBrandYoutube,
  },
  {
    id: 'gh',
    label: 'GitHub',
    link: 'https://github.com/',
    icon: IconBrandGithub,
  },
]

/**
 * Структура главного навигационного меню подвала, разбитая по тематическим колонкам.
 * Используется в компоненте `FooterNavigation` для генерации ссылок.
 */
export const sections: NavSection[] = [
  {
    title: 'Продукт',
    links: [
      { label: 'Вакансии и матчинг', link: '/app/vacancies' },
      { label: 'Резюме с AI-ревью', link: '/app/resume' },
      { label: 'Трекер откликов', link: '/app/tracker' },
      { label: 'AI-собеседования', link: '/app/ai-interview' },
      { label: 'Тарифы', link: '/#pricing' },
    ],
  },
  {
    title: 'Хекслет',
    links: [
      { label: 'Курсы и профессии', link: '#', isExternal: true },
      { label: 'Коммерческие проекты', link: '/app/commercial-projects' },
      { label: 'Сообщество', link: '#', isExternal: true },
      { label: 'Блог', link: '/blog' },
      { label: 'База вопросов с собеседований', link: '#', isExternal: true },
      { label: 'Помощь', link: '#', isExternal: true },
    ],
  },
  {
    title: 'Документы',
    links: [
      { label: 'Правовая информация', link: '/legal' },
      { label: 'Персональные данные', link: '/legal' },
      { label: 'Оферта', link: '/legal' },
      {
        label: 'careers@hexlet.io',
        link: 'mailto:careers@hexlet.io',
        isExternal: true,
      },
    ],
  },
]
