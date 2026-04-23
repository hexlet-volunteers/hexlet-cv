import { withMarketingLayout } from './components/withMarketingLayout'

/**
 * Пропсы для главной страницы маркетинга.
 */
interface PageProps {
  /** Дочерние элементы для отображения на странице. */
  children: React.ReactNode
}

/**
 * Главная страница раздела маркетинга в админ-панели.
 * Служит контейнером для дочерних элементов.
 *
 * @param props - Пропсы компонента.
 * @param props.children - Дочерние элементы для отображения.
 * @returns React-компонент главной страницы маркетинга.
 */
const MarketingPage = ({ children }: PageProps) => {
  return <>{children}</>
}

/**
 * Главная страница маркетинга, обернутая в макет.
 * Использует HOC withMarketingLayout для применения макетов.
 */
const MarketingPageWithLayout = withMarketingLayout(MarketingPage)

export default MarketingPageWithLayout
