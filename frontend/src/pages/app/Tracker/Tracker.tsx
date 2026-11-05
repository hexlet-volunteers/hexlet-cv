import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router'
import {
  Alert,
  Box,
  Button,
  Card,
  Center,
  Chip,
  Group,
  Loader,
  Paper,
  SegmentedControl,
  Select,
  Stack,
  Text,
  TextInput,
  Title,
} from '@mantine/core'
import { useDebouncedValue } from '@mantine/hooks'
import { IconPlus, IconSearch } from '@tabler/icons-react'
import { RequireAuth } from '@features/auth'
import {
  api,
  ApiError,
  type ApplicationList,
  type ApplicationStage,
  type ApplicationSummary,
  type OwnResume,
} from '@shared/api'
import { plural } from './lib/format'
import { BOARD_STAGES, STAGE_LABEL, STAGE_SHORT } from './lib/stages'
import { AddApplicationModal } from './ui/AddApplicationModal'
import { ApplicationDrawer } from './ui/ApplicationDrawer'
import { ApplicationsTable } from './ui/ApplicationsTable'
import { Board } from './ui/Board'

type View = 'board' | 'table'
const VIEW_KEY = 'tracker:view'

function readView(): View {
  try {
    return localStorage.getItem(VIEW_KEY) === 'table' ? 'table' : 'board'
  } catch {
    return 'board'
  }
}

/** Трекер откликов: доска, таблица, панель отклика и добавление (#1124). */
function TrackerPage() {
  const [view, setView] = useState<View>(readView)
  const [stage, setStage] = useState<'ALL' | ApplicationStage>('ALL')
  const [resumeId, setResumeId] = useState<string | null>(null)
  const [search, setSearch] = useState('')
  const [q] = useDebouncedValue(search, 250)
  const [reload, setReload] = useState(0)

  const [data, setData] = useState<ApplicationList | null>(null)
  const [items, setItems] = useState<ApplicationSummary[]>([])
  const [error, setError] = useState<string | null>(null)
  const [moveError, setMoveError] = useState<string | null>(null)

  const [resumes, setResumes] = useState<{ id: number; name: string }[]>([])
  const [openId, setOpenId] = useState<number | null>(null)
  const [adding, setAdding] = useState(false)

  useEffect(() => {
    try {
      localStorage.setItem(VIEW_KEY, view)
    } catch {
      // приватный режим — вид просто не запомнится
    }
  }, [view])

  useEffect(() => {
    api<OwnResume[]>('/api/account/resumes')
      .then((list) => setResumes(list.map((r) => ({ id: r.id, name: r.name }))))
      .catch(() => setResumes([]))
  }, [])

  useEffect(() => {
    let active = true
    const params = new URLSearchParams()
    if (stage !== 'ALL') params.set('stage', stage)
    if (resumeId) params.set('resumeId', resumeId)
    if (q.trim()) params.set('q', q.trim())
    api<ApplicationList>(`/api/account/applications?${params}`)
      .then((list) => {
        if (!active) return
        setData(list)
        setItems(list.items)
        setError(null)
      })
      .catch(() => active && setError('Не удалось загрузить отклики'))
    return () => {
      active = false
    }
  }, [stage, resumeId, q, reload])

  const refresh = () => setReload((n) => n + 1)

  /** Оптимистично двигаем карточку, при ошибке возвращаем на место. */
  const move = async (id: number, to: ApplicationStage) => {
    const before = items
    setItems((list) => list.map((a) => (a.id === id ? { ...a, stage: to } : a)))
    setMoveError(null)
    try {
      await api(`/api/account/applications/${id}/stage`, {
        method: 'PATCH',
        body: { stage: to },
      })
      refresh()
    } catch (err) {
      setItems(before)
      setMoveError(
        err instanceof ApiError && err.status !== 0
          ? err.message
          : 'Не удалось перенести карточку',
      )
    }
  }

  const active = useMemo(
    () =>
      data
        ? BOARD_STAGES.filter((s) => s !== 'SAVED').reduce(
            (n, s) => n + data.counts[s],
            0,
          )
        : 0,
    [data],
  )

  if (!data && !error) {
    return (
      <Center py={120}>
        <Loader />
      </Center>
    )
  }

  const counts = data?.counts
  const hasAny = counts
    ? counts.ALL + counts.REJECTED > 0 ||
      Boolean(q || resumeId || stage !== 'ALL')
    : false

  return (
    <Stack gap="lg">
      <Group justify="space-between" align="flex-start">
        <Box>
          <Title order={1} fz={30}>
            Отклики
          </Title>
          <Text c="dimmed" size="sm">
            {active} {plural(active, 'активный', 'активных', 'активных')} ·
            нажмите на карточку: внутри вся связка — этапы, резюме, письма,
            напоминания
          </Text>
        </Box>
        <Group>
          <SegmentedControl
            value={view}
            onChange={(v) => setView(v as View)}
            data={[
              { label: 'Доска', value: 'board' },
              { label: 'Таблица', value: 'table' },
            ]}
          />
          <Button
            leftSection={<IconPlus size={16} />}
            onClick={() => setAdding(true)}
          >
            Добавить отклик
          </Button>
        </Group>
      </Group>

      {error && <Alert color="red">{error}</Alert>}
      {moveError && (
        <Alert color="red" withCloseButton onClose={() => setMoveError(null)}>
          {moveError}
        </Alert>
      )}

      {counts && (
        <Group justify="space-between" gap="sm">
          <Group gap="sm">
            <TextInput
              placeholder="Компания или роль…"
              leftSection={<IconSearch size={16} />}
              value={search}
              onChange={(e) => setSearch(e.currentTarget.value)}
              w={240}
              aria-label="Поиск по компании или роли"
            />
            <Chip.Group
              value={stage}
              onChange={(v) => setStage(v as typeof stage)}
            >
              <Group gap={6}>
                <Chip value="ALL" variant="outline" color="dark">
                  Все · {counts.ALL}
                </Chip>
                {BOARD_STAGES.map((s) => (
                  <Chip key={s} value={s} variant="outline" color="dark">
                    {STAGE_SHORT[s]} {counts[s]}
                  </Chip>
                ))}
              </Group>
            </Chip.Group>
          </Group>
          <Select
            w={220}
            placeholder="Резюме: все"
            data={resumes.map((r) => ({
              value: String(r.id),
              label: `Резюме: ${r.name}`,
            }))}
            value={resumeId}
            onChange={setResumeId}
            clearable
            aria-label="Фильтр по версии резюме"
          />
        </Group>
      )}

      {!hasAny ? (
        <Card withBorder radius="md" p="xl">
          <Text fw={700} fz="lg">
            Пока ни одного отклика
          </Text>
          <Text c="dimmed" mt={4} mb="md" maw={560}>
            Добавьте вакансии, на которые откликаетесь или собираетесь. Трекер
            напомнит о фоллоу-апе, если компания молчит, и о дедлайне, если
            вакансия скоро закроется.
          </Text>
          <Button
            leftSection={<IconPlus size={16} />}
            onClick={() => setAdding(true)}
            w="fit-content"
          >
            Добавить первый отклик
          </Button>
        </Card>
      ) : view === 'board' ? (
        <Board
          items={items}
          onOpen={setOpenId}
          onMove={(id, to) => void move(id, to)}
        />
      ) : items.length > 0 ? (
        <Paper withBorder radius="md" p="sm">
          <ApplicationsTable items={items} onOpen={setOpenId} />
        </Paper>
      ) : (
        <Paper withBorder radius="md" p="xl">
          <Text c="dimmed" ta="center">
            {stage === 'ALL'
              ? 'Ничего не нашлось'
              : `На этапе «${STAGE_LABEL[stage]}» откликов нет`}{' '}
            — измените поиск или фильтры.
          </Text>
        </Paper>
      )}

      {data && data.rejections.total > 0 && (
        <RejectionsBar rejections={data.rejections} />
      )}

      <ApplicationDrawer
        id={openId}
        onClose={() => setOpenId(null)}
        onChanged={refresh}
      />
      <AddApplicationModal
        opened={adding}
        onClose={() => setAdding(false)}
        resumes={resumes}
        onCreated={() => {
          setStage('ALL')
          refresh()
        }}
      />
    </Stack>
  )
}

function RejectionsBar({
  rejections,
}: {
  rejections: ApplicationList['rejections']
}) {
  const [top] = (
    Object.entries(rejections.byStage) as [ApplicationStage, number][]
  ).sort((a, b) => b[1] - a[1])
  return (
    <Paper withBorder radius="md" p="md">
      <Group justify="space-between">
        <Text size="sm" c="dimmed">
          <Text span fw={700} c="dark">
            Отказы · {rejections.total}
          </Text>
          {top && (
            <>
              {'  '}
              {top[1]} после этапа «{STAGE_LABEL[top[0]]}»
            </>
          )}
        </Text>
        <Button
          component={Link}
          to="/app/analytics"
          variant="default"
          size="xs"
        >
          Разобрать причины
        </Button>
      </Group>
    </Paper>
  )
}

export function Tracker() {
  return (
    <RequireAuth>
      <TrackerPage />
    </RequireAuth>
  )
}
