import { Link } from '@inertiajs/react'
import { useState } from 'react'
import { Text, Button, Pagination, Paper } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import classes from './Favorite.module.css'

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
  const itemsPerPage: number = 3
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
          <Paper
            shadow="xs"
            radius="lg"
            withBorder
            p={0}
            bg="var(--mantine-color-dark-5)"
            bd="1px solid var(--mantine-color-gray-6)"
          >
            <Text>
              {t(
                'accountPage.favorites.noFavorites',
                'Нет избранных материалов',
              )}
            </Text>
          </Paper>
        ) : (
          <>
            <Paper
              shadow="xs"
              radius="lg"
              withBorder
              p={0}
              bg="var(--mantine-color-dark-5)"
              bd="1px solid var(--mantine-color-gray-6)"
            >
              <ul className={classes.list}>
                {paginatedList?.map((item) => (
                  <li key={item.id} className={classes.listItem}>
                    <p>{item.title}</p>
                    <Button
                      component={Link}
                      href={item.url}
                      variant="light"
                      color="blue"
                      size="md"
                      radius="lx"
                    >
                      {t('accountPage.favorites.butOpen', 'Открыть')}
                    </Button>
                  </li>
                ))}
              </ul>
              {showPagination && (
                <div className={classes.paginationWrapper}>
                  <Pagination
                    value={activePage}
                    onChange={setPage}
                    total={totalPages}
                    classNames={{
                      control: classes.paginationControl,
                    }}
                  />
                </div>
              )}
            </Paper>
          </>
        )}
      </div>
    </div>
  )
}

export default Favorites
