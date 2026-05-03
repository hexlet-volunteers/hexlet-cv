import { Link } from '@inertiajs/react'
import { useState } from 'react'
import { Text, Button, Pagination, Paper, Table } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import classes from './Favorite.module.css'

/**
 * Объект, представляющий один избранный материал (курс или статью).
 */
export type FavoriteDTO = {
  id: number
  type: 'course' | 'article'
  title: string
  url: string
}

/**
 * Свойства компонента Favorites.
 */
export interface FavoritesProps {
  list?: FavoriteDTO[]
}

const Favorites = ({ list }: FavoritesProps) => {
  const { t } = useTranslation()
  const [activePage, setPage] = useState(1)
  const itemsPerPage: number = 6
  const paginatedList = list?.slice(
    (activePage - 1) * itemsPerPage,
    activePage * itemsPerPage,
  )
  const totalPages: number = Math.ceil((list?.length || 0) / itemsPerPage)
  const showPagination: boolean = totalPages > 1 // если страниц больше 1

  if (!list || list?.length === 0) {
    return (
      <Paper
        shadow="xs"
        radius="lg"
        withBorder
        p={0}
        bg="var(--mantine-color-dark-5)"
        bd="1px solid var(--mantine-color-gray-6)"
      >
        <Text>{t('accountPage.favorites.noFavorites')}</Text>
      </Paper>
    )
  }
  const rows = paginatedList?.map((item) => (
    <Table.Tr key={item.id}>
      <Table.Td>
        {item.type === 'course'
          ? t('accountPage.favorites.course')
          : t('accountPage.favorites.article')}
      </Table.Td>
      <Table.Td>{item.title}</Table.Td>
      <Table.Td style={{ display: 'flex', justifyContent: 'flex-end' }}>
        <Button
          component={Link}
          href={item.url}
          variant="light"
          color="blue"
          size="md"
          radius="lx"
        >
          {t('accountPage.favorites.butOpen')}
        </Button>
      </Table.Td>
    </Table.Tr>
  ))

  return (
    <Paper
      shadow="xs"
      radius="lg"
      withBorder
      p={0}
      bg="var(--mantine-color-dark-5)"
      bd="1px solid var(--mantine-color-gray-6)"
    >
      <Table withColumnBorders>
        <Table.Tbody>{rows}</Table.Tbody>
      </Table>
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
  )
}

export default Favorites
