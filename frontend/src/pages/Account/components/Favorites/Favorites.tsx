import { Link } from '@inertiajs/react'
import { useState } from 'react'
import { Text, Button } from '@mantine/core'
import { Pagination } from '@mantine/core'

import { useTranslation } from 'react-i18next'

export interface FavoritesProps {
  list?: Array<{
    id: number
    type: 'course' | 'article'
    title: string
    url: string
  }>
}

const Favorites = ({ list }: FavoritesProps) => {
  const { t } = useTranslation()
  const [activePage, setPage] = useState(1)
  const itemsPerPage: number = 10
  const paginatedList = list?.slice(
    (activePage - 1) * itemsPerPage,
    activePage * itemsPerPage,
  )
  const totalPages: number = Math.ceil((list?.length || 0) / itemsPerPage)
  const showPagination: boolean = totalPages > 1 // если страниц больше 1

  return (
    <div>
      <div>
        {!list || list?.length === 0 ? (
          <Text>
            {t('accountPage.favorites.noFavorites', 'Нет избранных материалов')}
          </Text>
        ) : (
          <>
            <ul>
              {paginatedList?.map((item) => (
                <li key={item.id}>
                  <p>{item.title}</p>
                  <Button component={Link} href={item.url}>
                    {t('accountPage.favorites.butOpen', 'Открыть')}
                  </Button>
                </li>
              ))}
            </ul>
            {showPagination && (
              <Pagination
                value={activePage}
                onChange={setPage}
                total={totalPages}
              />
            )}
          </>
        )}
      </div>
    </div>
  )
}

export default Favorites
