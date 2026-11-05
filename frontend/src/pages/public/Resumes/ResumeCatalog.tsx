import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router'
import { Container, Group, Pagination, Stack, Text, TextInput, Title } from '@mantine/core'
import { useDebouncedValue } from '@mantine/hooks'
import { IconSearch } from '@tabler/icons-react'
import { api, useApi, type Page, type ResumeSummary } from '@shared/api'
import { PageState } from './ui/PageState'
import { ResumeCard } from './ui/ResumeCard'

/**
 * Каталог опубликованных резюме с поиском — замена главной старого сайта (`/ru?q=`).
 * Запрос и страница живут в адресе, чтобы ссылкой на поиск можно было поделиться.
 */
export function ResumeCatalog() {
  const [params, setParams] = useSearchParams()
  const q = params.get('q') ?? ''
  const page = Math.max(Number(params.get('page')) || 1, 1)

  const [input, setInput] = useState(q)
  const [debounced] = useDebouncedValue(input, 300)

  useEffect(() => {
    if (debounced !== q) {
      setParams(debounced ? { q: debounced } : {}, { replace: true })
    }
  }, [debounced, q, setParams])

  const query = new URLSearchParams({ q, page: String(page) })
  const { data, error, loading } = useApi(() => api<Page<ResumeSummary>>(`/api/resumes?${query}`), [q, page])

  const goTo = (p: number) => {
    setParams({ ...(q ? { q } : {}), page: String(p) })
    window.scrollTo({ top: 0 })
  }

  return (
    <Container size="md" py={48}>
      <Title order={1} fz={36} mb={8}>
        Резюме программистов
      </Title>
      <Text c="dimmed" mb="xl">
        Опубликованные резюме студентов и выпускников Хекслета.
      </Text>

      <TextInput
        size="md"
        placeholder="Должность или город, например «Java» или «Казань»"
        leftSection={<IconSearch size={18} />}
        value={input}
        onChange={(e) => setInput(e.currentTarget.value)}
        mb="lg"
        aria-label="Поиск резюме"
      />

      {loading || error ? (
        <PageState loading={loading} error={error} />
      ) : data && data.items.length > 0 ? (
        <>
          <Text fz="sm" c="dimmed" mb="sm">
            Найдено: {data.totalItems}
          </Text>
          <Stack gap="md">
            {data.items.map((r) => (
              <ResumeCard key={r.id} resume={r} />
            ))}
          </Stack>
          {data.totalPages > 1 && (
            <Group justify="center" mt="xl">
              <Pagination total={data.totalPages} value={data.page} onChange={goTo} />
            </Group>
          )}
        </>
      ) : (
        <Text c="dimmed" ta="center" py={60}>
          {q ? `По запросу «${q}» ничего не нашлось` : 'Пока нет опубликованных резюме'}
        </Text>
      )}
    </Container>
  )
}
