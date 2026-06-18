import { AppLayout } from '../AppLayout'
import { Container, ScrollArea, ThemeIcon } from '@mantine/core'
import { IconHeart } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import { PageHeader } from '@widgets/page-header'
import type { InertiaPage } from '@shared/types/inertia'
import Favorites from './Favorites'

import type { FavoriteDTO } from '@entities/favorite'

/**
 * Inertia-страница избранных материалов пользователя.
 *
 * Получает список избранного из page props и отображает его
 * в разделе личного кабинета `/account/favorites`.
 */

interface FavoritesPageProps {
  list?: FavoriteDTO[]
}

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
