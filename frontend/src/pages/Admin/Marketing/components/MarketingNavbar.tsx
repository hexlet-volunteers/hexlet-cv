import { Link, router, usePage } from '@inertiajs/react'
import { SegmentedControl } from '@mantine/core'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'

type sectionsType = 'articles' | 'stories' | 'reviews' | 'team' | 'pricing'

const sections: sectionsType[] = ['articles', 'stories', 'reviews', 'team', 'pricing']

export interface LinkProps {
  section: sectionsType
  activeSection: sectionsType
}

const getCurrentSection = (url: string): sectionsType =>
  sections.find(s => url.includes(`/marketing/${s}`)) || 'articles'

const NavbarLink: React.FC<LinkProps> = (props) => {
  const { section, activeSection } = props
  const { t } = useTranslation()

  return (
    <Link
      href={`/admin/marketing/${section}`}
      data-active={activeSection === section}
      style={{ color: 'inherit',
        textDecoration: 'none',
      }}
    >
      {t(`adminPage.marketing.menu.${section}`)}
    </Link>
  )
}

export const MarketingNavbar: React.FC = () => {
  const { url } = usePage()

  const [activeSection, setActiveSection] = useState<sectionsType>(getCurrentSection(url))

  const menuData = sections.map(section => ({
    label: <NavbarLink section={section} activeSection={activeSection} />,
    value: section,
  }))

  useEffect(() => {
    if (url.endsWith('/admin/marketing')) {
      router.visit('/admin/marketing/articles')
    }

    const newSection = getCurrentSection(url)
    setActiveSection(newSection)
  }, [url])

  return (
    <SegmentedControl
      value={activeSection}
      data={menuData}
      onChange={value => router.visit(`/admin/marketing/${value}`)}
    />
  )
}

export default MarketingNavbar
