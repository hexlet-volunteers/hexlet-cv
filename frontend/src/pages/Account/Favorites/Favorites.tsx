import { Link } from '@inertiajs/react'
import { useState } from 'react'
import {
  Text,
  Button,
  Pagination,
  Paper,
  Table,
  Box,
  Skeleton,
} from '@mantine/core'
import { useTranslation } from 'react-i18next'
import type { FavoriteDTO } from '@entities/favorite'

/**
 * Свойства компонента Favorites.
 */

export interface FavoritesProps {
  list?: FavoriteDTO[]
  isLoading?: boolean
}

const Favorites = ({ list, isLoading = false }: FavoritesProps) => {
  const { t } = useTranslation()
  const [activePage, setPage] = useState(1)
  const itemsPerPage: number = 6
  const paginatedList = list?.slice(
    (activePage - 1) * itemsPerPage,
    activePage * itemsPerPage,
  )
  const totalPages = Math.ceil((list?.length || 0) / itemsPerPage)
  const showPagination = totalPages > 1 // если страниц больше 1

  //TODO: сделать серверную пагинацию

  if (isLoading) {
    return (
      <Paper shadow="xs" radius="lg" withBorder p="md">
        <Table withColumnBorders>
          <Table.Thead>
            <Table.Tr>
              <Table.Th style={{ width: '15%' }}>
                <Skeleton height={24} width="60%" />
              </Table.Th>
              <Table.Th style={{ width: '70%' }}>
                <Skeleton height={24} width="80%" />
              </Table.Th>
              <Table.Th
                style={{
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                }}
              >
                <Skeleton height={24} width="80%" />
              </Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {Array.from({ length: 6 }).map((_, idx) => (
              <Table.Tr key={idx}>
                <Table.Td>
                  <Skeleton height={20} width="50%" />
                </Table.Td>
                <Table.Td>
                  <Skeleton height={20} width="80%" />
                </Table.Td>
                <Table.Td
                  style={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                  }}
                >
                  <Skeleton height={36} width={100} radius="xl" />
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      </Paper>
    )
  }

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
