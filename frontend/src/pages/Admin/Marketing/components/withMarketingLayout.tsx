import React from 'react'
import { MarketingLayout } from './MarketingLayout'
import { AdminLayout } from '../../components/AdminLayout'

/**
 * HOC (Higher-Order Component) для обертки компонентов страниц маркетинга.
 * Оборачивает компонент в MarketingLayout, который в свою очередь
 * оборачивается в AdminLayout.
 *
 * @template P - Тип пропсов оборачиваемого компонента.
 * @param WrappedComponent - Компонент, который нужно обернуть в макет.
 * @returns Компонент, обернутый в макеты маркетинга и админ-панели.
 */
export function withMarketingLayout<P extends object>(
  WrappedComponent: React.ComponentType<P>,
) {
  /**
   * Компонент с примененными макетами.
   *
   * @param props - Пропсы для оборачиваемого компонента.
   * @returns React-компонент с макетами.
   */
  const ComponentWithLayout = (props: P) => {
    return (
      <MarketingLayout>
        <WrappedComponent {...props} />
      </MarketingLayout>
    )
  }

  /**
   * Статическое свойство layout для интеграции с Inertia.js.
   * Оборачивает страницу в AdminLayout.
   *
   * @param page - React-элемент страницы.
   * @returns Страница, обернутая в AdminLayout.
   */
  ComponentWithLayout.layout = (page: React.ReactNode) => (
    <AdminLayout>{page}</AdminLayout>
  )

  return ComponentWithLayout
}
