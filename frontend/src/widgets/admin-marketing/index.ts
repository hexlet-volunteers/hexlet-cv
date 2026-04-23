/**
 * Модуль виджетов для админ-панели маркетинга.
 *
 * Этот модуль содержит компоненты для управления маркетинговым контентом
 * в административной панели, включая статьи, истории, отзывы, команду и ценообразование.
 *
 * @module admin-marketing
 */

/** Компонент для управления статьями. */
export { MarketingArticles } from './ui/MarketingArticles'
/** Компонент для управления историями (stories). */
export { MarketingStories } from './ui/MarketingStories'
/** Компонент для управления отзывами. */
export { MarketingReviews } from './ui/MarketingReviews'
/** Компонент для управления командой. */
export { MarketingTeam } from './ui/MarketingTeam'
/** Компонент для управления ценообразованием. */
export { MarketingPricing } from './ui/MarketingPricing'
/** Универсальный компонент-обертка для таблиц маркетинговых данных. */
export { MarketingTable } from './ui/MarketingTable'

/** Пропсы для компонента MarketingArticles. */
export type { TProps as ArticlesProps } from './ui/MarketingArticles'
/** Пропсы для компонента MarketingStories. */
export type { TProps as StoriesProps } from './ui/MarketingStories'
/** Пропсы для компонента MarketingReviews. */
export type { TProps as ReviewsProps } from './ui/MarketingReviews'
/** Пропсы для компонента MarketingTeam. */
export type { TProps as TeamProps } from './ui/MarketingTeam'
/** Пропсы для компонента MarketingPricing. */
export type { TProps as PricingProps } from './ui/MarketingPricing'

/** Тип данных для статьи. */
export type { MarketingArticlesDTO } from './ui/MarketingArticles'
/** Тип данных для истории (story). */
export type { MarketingStoriesDTO } from './ui/MarketingStories'
/** Тип данных для отзыва. */
export type { MarketingReviewsDTO } from './ui/MarketingReviews'
/** Тип данных для участника команды. */
export type { MarketingTeamDTO } from './ui/MarketingTeam'
/** Тип данных для элемента ценообразования. */
export type { MarketingPricingDTO } from './ui/MarketingPricing'
