import { AppLayout } from '../AppLayout'
import { Container, ScrollArea, ThemeIcon } from '@mantine/core'
import { IconHeart } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import { PageHeader } from '@widgets/page-header'
import type { InertiaPage } from '@shared/types/inertia'
import Favorites from './Favorites'

/**
 * Свойства компонента {@link Favorites}.
 */
interface FavoritesPageProps {
  /**
   * Список избранных материалов пользователя.
   *
   * @remarks
   * Каждый элемент содержит идентификатор, тип (курс или статья),
   * название и ссылку для перехода.
   */
  list?: Array<{
    id: number
    type: 'course' | 'article'
    title: string
    url: string
  }>
}

/**
 * Компонент отображает список избранных материалов пользователя.
 *
 * @remarks
 * Используется в личном кабинете на странице `/account/favorites`.
 * При пустом списке показывает сообщение "Нет избранных материалов".
 *
 * @param props - Свойства компонента (см. {@link FavoritesProps}).
 * @returns React-элемент со списком карточек избранного.
 *
 * @example
 * ```tsx
 * <FavoritesPage={mockFavoritesList} />
 * ```
 */
const FavoritesPage: InertiaPage<FavoritesPageProps> = ({ list }) => {
  const { t } = useTranslation()

  return (
    <Container fluid>
      <PageHeader
        icon={
          <ThemeIcon size="xl" variant="light" color="blue">
            <IconHeart />
          </ThemeIcon>
        }
        title={t('accountPage.favorites.title')}
      />
      <ScrollArea h={600}>
        <Favorites list={list} />
      </ScrollArea>
    </Container>
  )
}

FavoritesPage.layout = (page) => <AppLayout>{page}</AppLayout>

export default FavoritesPage
