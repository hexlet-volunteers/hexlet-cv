import { Link } from '@inertiajs/react'
import { useState } from 'react'
import { Text, Button, Pagination, Paper, Table, Box } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import type { FavoriteDTO } from '@entities/favorite'

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
  const totalPages = Math.ceil((list?.length || 0) / itemsPerPage)
  const showPagination = totalPages > 1 // если страниц больше 1

  if (!list || list?.length === 0) {
    return (
      <Paper shadow="xs" radius="lg" withBorder p="md">
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
      <Table.Td
        style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
        }}
      >
        <Button
          component={Link}
          href={item.url}
          variant="light"
          color="blue"
          size="md"
          radius="xl"
        >
          {t('accountPage.favorites.butOpen')}
        </Button>
      </Table.Td>
    </Table.Tr>
  ))

  return (
    <Paper shadow="xs" radius="lg" withBorder p="md">
      <Table withColumnBorders>
        <Table.Thead>
          <Table.Tr>
            <Table.Th style={{ width: '1%', whiteSpace: 'nowrap' }}>
              {t('accountPage.favorites.type')}
            </Table.Th>
            <Table.Th>{t('accountPage.favorites.titleType')}</Table.Th>
            <Table.Th
              style={{ textAlign: 'center', width: '1%', whiteSpace: 'nowrap' }}
            >
              {t('accountPage.favorites.actions')}
            </Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>{rows}</Table.Tbody>
      </Table>
      {showPagination && (
        <Box
          style={{
            borderTop: '1px solid var(--mantine-color-default-border)',
            paddingTop: 'var(--mantine-spacing-md)',
            marginTop: 'var(--mantine-spacing-md)',
          }}
        >
          <Pagination
            value={activePage}
            onChange={setPage}
            total={totalPages}
            color="blue"
          />
        </Box>
      )}
    </Paper>
  )
}

export default Favorites
