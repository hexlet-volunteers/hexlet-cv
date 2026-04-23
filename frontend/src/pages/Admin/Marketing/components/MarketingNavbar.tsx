import { Link, usePage } from '@inertiajs/react'
import { SegmentedControl } from '@mantine/core'
import React from 'react'
import { useTranslation } from 'react-i18next'

/**
 * Тип для обозначения секций навигации в маркетинговом разделе.
 */
type sectionsType = 'articles' | 'stories' | 'reviews' | 'team' | 'pricing'

/**
 * Массив доступных секций навигации.
 */
const sections: sectionsType[] = [
  'articles',
  'stories',
  'reviews',
  'team',
  'pricing',
]

/**
 * Пропсы для компонента ссылки навигации.
 */
export interface LinkProps {
  /** Название секции для перехода. */
  section: sectionsType
  /** Текущая активная секция. */
  activeSection: sectionsType
}

/**
 * Определяет текущую секцию на основе URL страницы.
 *
 * @param url - Текущий URL страницы.
 * @returns Название текущей секции или 'articles' по умолчанию.
 */
const getCurrentSection = (url: string): sectionsType => {
  const parts = url.split('/').filter(Boolean)

  const index = parts.indexOf('marketing')
  const section = parts[index + 1] as sectionsType

  return sections.includes(section) ? section : 'articles'
}

/**
 * Компонент ссылки для навигационного меню.
 *
 * @param props - Пропсы компонента.
 * @param props.section - Секция для перехода.
 * @param props.activeSection - Текущая активная секция.
 * @returns React-компонент ссылки.
 */
const NavbarLink: React.FC<LinkProps> = (props) => {
  const { section, activeSection } = props
  const { t } = useTranslation()

  return (
    <Link
      href={`/admin/marketing/${section}`}
      data-active={activeSection === section}
      style={{ color: 'inherit', textDecoration: 'none' }}
    >
      {t(`adminPage.marketing.menu.${section}`)}
    </Link>
  )
}

/**
 * Компонент навигационной панели для раздела маркетинга.
 * Отображает сегментированный контроль с переключением между секциями:
 * статьи, истории, отзывы, команда и ценообразование.
 *
 * @returns React-компонент навигационной панели.
 */
export const MarketingNavbar: React.FC = () => {
  const { url } = usePage()

  const activeSection = getCurrentSection(url)

  const menuData = sections.map((section) => ({
    label: <NavbarLink section={section} activeSection={activeSection} />,
    value: section,
  }))

  return <SegmentedControl value={activeSection} data={menuData} />
}

export default MarketingNavbar
