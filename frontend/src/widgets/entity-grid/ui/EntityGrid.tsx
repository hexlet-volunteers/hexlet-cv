import { SimpleGrid, Group, Pagination } from '@mantine/core'
import { EmptyPlaceholder } from '@shared/ui'
import { router } from '@inertiajs/react'
import type { TPagination } from '@shared/types'

export interface IEmptyPlaceholderConfig {
  title: string
  icon: React.ElementType
  buttonLink: string
  buttonLabel: string
}

interface IProps<T> {
  data: T[]
  pagination: TPagination
  emptyConfig: IEmptyPlaceholderConfig
  baseUrl: string
  renderItem: (dataItem: T) => React.ReactNode
}

export const EntityGrid = <T extends { id: string | number }>(
  props: IProps<T>
) => {
  const { data, pagination, emptyConfig, baseUrl, renderItem } = props
  // API ожидает индекс страницы с 0, поэтому вычитаем 1 из значения пагинатора
  const handlePageChange = (page: number) => {
    router.get(baseUrl, { page: page - 1 }, { preserveScroll: true })
  }
  if (!data.length) return <EmptyPlaceholder {...emptyConfig} />

  return (
    <>
      <SimpleGrid
        cols={{
          base: 1,
          lg: 3,
        }}
        spacing="md"
      >
        {data.map(dataItem => (
          <div key={dataItem.id}>{renderItem(dataItem)}</div>
        ))}
      </SimpleGrid>

      {/* пагинация */}
      {pagination.totalPages > 1 && (
        <Group justify="center" mt="xl" pb="xl">
          <Pagination
            total={pagination.totalPages}
            value={pagination.currentPage + 1}
            onChange={handlePageChange}
          />
        </Group>
      )}
    </>
  )
}
