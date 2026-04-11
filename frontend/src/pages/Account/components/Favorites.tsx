import { Link } from '@inertiajs/react'
import { useState } from 'react'
import { Text, Button, Group } from '@mantine/core'
import { Pagination } from '@mantine/core'
import { IconHeart } from '@tabler/icons-react'

/**
{
"component": "Favorites",
"props": { 
"list": "Array<
{ id: number,
  type: 'course' | 'article',
  title: string,
  url: string }> },
"url": "/account/favorites",
}
 */

export interface FavoritesProps {
  list?: Array<{
    id: number
    type: 'course' | 'article'
    title: string
    url: string
  }>
}

const Favorites = ({ list }: FavoritesProps) => {
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
      <Group mb="md">
        <IconHeart size={24} />
        <Text fz="xl" fw={700}>
          Избранное
        </Text>
      </Group>

      <div>
        {!list || list?.length === 0 ? (
          <Text>Нет избранных материалов</Text>
        ) : (
          <>
            <ul>
              {paginatedList?.map((item) => (
                <li key={item.id}>
                  <p>{item.title}</p>
                  <Button component={Link} href={item.url}>
                    Открыть
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
