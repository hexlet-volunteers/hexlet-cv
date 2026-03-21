import type { AdminMenuDTO } from '@pages/Admin/components/AdminNavbar'

export const mockMenu: AdminMenuDTO[] = [
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
