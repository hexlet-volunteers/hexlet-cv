import { useEffect, useState, type FormEvent } from 'react'
import { Link } from 'react-router'
import {
  Alert,
  Anchor,
  Badge,
  Box,
  Button,
  Center,
  Collapse,
  Drawer,
  Group,
  Loader,
  Paper,
  Select,
  SimpleGrid,
  Stack,
  Text,
  TextInput,
  Textarea,
  Timeline,
  Title,
} from '@mantine/core'
import { IconExternalLink, IconFileText, IconMail } from '@tabler/icons-react'
import {
  api,
  ApiError,
  type ApplicationDetails,
  type ApplicationEventType,
  type ApplicationStage,
  type FieldErrors,
} from '@shared/api'
import { shortDate, toApiDateTime, toLocalInput, when } from '../lib/format'
import { STAGE_COLOR, STAGE_LABEL } from '../lib/stages'
import { CompanyAvatar } from './CompanyAvatar'

const EVENT_TYPES: { value: ApplicationEventType; label: string }[] = [
  { value: 'REPLY', label: 'Ответ компании' },
  { value: 'INTERVIEW', label: 'Интервью' },
  { value: 'LETTER', label: 'Письмо' },
  { value: 'NOTE', label: 'Заметка' },
]

const STAGE_OPTIONS = (Object.keys(STAGE_LABEL) as ApplicationStage[]).map(
  (s) => ({
    value: s,
    label: STAGE_LABEL[s],
  }),
)

interface Props {
  id: number | null
  onClose: () => void
  /** Отклик изменился — доска и таблица перезагружают список. */
  onChanged: () => void
}

/** Боковая панель отклика: этап, следующий шаг, документы, история (#1158). */
export function ApplicationDrawer({ id, onClose, onChanged }: Props) {
  const [app, setApp] = useState<ApplicationDetails | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    setApp(null)
    setError(null)
    if (id === null) return
    api<ApplicationDetails>(`/api/account/applications/${id}`)
      .then(setApp)
      .catch(() => setError('Не удалось загрузить отклик'))
  }, [id])

  const updated = (next: ApplicationDetails) => {
    setApp(next)
    onChanged()
  }

  return (
    <Drawer
      opened={id !== null}
      onClose={onClose}
      position="right"
      size={520}
      padding="lg"
      withCloseButton
      title={app ? <Header app={app} /> : null}
    >
      {error && <Alert color="red">{error}</Alert>}
      {!app && !error && (
        <Center py={80}>
          <Loader />
        </Center>
      )}
      {app && (
        <Body
          app={app}
          onUpdated={updated}
          onDeleted={() => {
            onChanged()
            onClose()
          }}
        />
      )}
    </Drawer>
  )
}

function Header({ app }: { app: ApplicationDetails }) {
  return (
    <Group gap="sm" wrap="nowrap">
      <CompanyAvatar company={app.company} size={44} />
      <Stack gap={0}>
        <Title order={3} fz={20}>
          {app.position}
        </Title>
        <Text size="sm" c="dimmed">
          {[app.company, app.salary].filter(Boolean).join(' · ')}
        </Text>
      </Stack>
    </Group>
  )
}

function Body({
  app,
  onUpdated,
  onDeleted,
}: {
  app: ApplicationDetails
  onUpdated: (a: ApplicationDetails) => void
  onDeleted: () => void
}) {
  const [editing, setEditing] = useState(false)
  const base = `/api/account/applications/${app.id}`

  const changeStage = async (stage: string | null) => {
    if (!stage || stage === app.stage) return
    onUpdated(
      await api<ApplicationDetails>(`${base}/stage`, {
        method: 'PATCH',
        body: { stage },
      }),
    )
  }

  const remove = async () => {
    if (!window.confirm(`Удалить отклик «${app.position}» в ${app.company}?`))
      return
    await api<void>(base, { method: 'DELETE' })
    onDeleted()
  }

  const step = app.nextStepAt
    ? [when(app.nextStepAt), app.nextStepTitle].filter(Boolean).join(' — ')
    : app.nextStepTitle

  return (
    <Stack gap="lg">
      <Group gap="xs">
        <Badge
          tt="none"
          variant="light"
          color={STAGE_COLOR[app.stage]}
          radius="sm"
          size="lg"
        >
          {STAGE_LABEL[app.stage]}
        </Badge>
        {app.source && (
          <Badge variant="light" color="gray" radius="sm" size="lg" tt="none">
            источник: {app.source}
          </Badge>
        )}
      </Group>

      <Select
        label="Этап"
        data={STAGE_OPTIONS}
        value={app.stage}
        onChange={changeStage}
        allowDeselect={false}
      />

      {step && (
        <Paper
          p="md"
          radius="md"
          bg="yellow.0"
          style={{ border: '1px solid var(--mantine-color-yellow-4)' }}
        >
          <Text size="xs" fw={800} c="yellow.9" tt="uppercase" mb={4}>
            Следующий шаг
          </Text>
          <Text>{step}</Text>
        </Paper>
      )}

      <Box>
        <Text size="xs" fw={800} c="dimmed" tt="uppercase" mb="xs">
          Связанные документы
        </Text>
        <Stack gap="xs">
          <DocumentRow
            icon={<IconFileText size={18} />}
            title={
              app.resume ? `Резюме ${app.resume.name}` : 'Резюме не привязано'
            }
            hint={
              app.resume
                ? 'именно эта версия ушла в отклик'
                : 'выберите версию в «Редактировать»'
            }
            to={app.resume ? `/app/resumes/${app.resume.id}` : undefined}
          />
          <DocumentRow
            icon={<IconMail size={18} />}
            title="Письма по отклику"
            hint="вся переписка по отклику — в «Письмах»"
            to={`/app/letters?application=${app.id}`}
          />
          {app.vacancyUrl && (
            <DocumentRow
              icon={<IconExternalLink size={18} />}
              title="Вакансия"
              hint={app.vacancyUrl}
              href={app.vacancyUrl}
            />
          )}
        </Stack>
      </Box>

      <Box>
        <Text size="xs" fw={800} c="dimmed" tt="uppercase" mb="sm">
          История этапов
        </Text>
        <Timeline active={app.events.length - 1} bulletSize={12} lineWidth={2}>
          {app.events.map((e) => (
            <Timeline.Item key={e.id}>
              <Text size="xs" c="dimmed">
                {shortDate(e.occurredAt)}
              </Text>
              <Text size="sm">{e.text}</Text>
            </Timeline.Item>
          ))}
        </Timeline>
        <AddEvent base={base} onAdded={onUpdated} />
      </Box>

      <Group grow>
        <Button component={Link} to={`/app/letters?application=${app.id}`}>
          Написать письмо
        </Button>
        <Button
          component={Link}
          to={`/app/ai-interview?application=${app.id}`}
          variant="default"
        >
          Тренировка к собесу
        </Button>
      </Group>

      <Group justify="space-between">
        <Button
          variant="subtle"
          size="xs"
          onClick={() => setEditing((v) => !v)}
        >
          {editing ? 'Скрыть редактирование' : 'Редактировать'}
        </Button>
        <Button
          variant="subtle"
          color="red"
          size="xs"
          onClick={() => void remove()}
        >
          Удалить отклик
        </Button>
      </Group>
      <Collapse in={editing}>
        <EditForm app={app} base={base} onSaved={onUpdated} />
      </Collapse>
    </Stack>
  )
}

function DocumentRow(props: {
  icon: React.ReactNode
  title: string
  hint: string
  to?: string
  href?: string
}) {
  const action = props.to ? (
    <Anchor component={Link} to={props.to} size="sm" fw={600}>
      Открыть
    </Anchor>
  ) : props.href ? (
    <Anchor
      href={props.href}
      target="_blank"
      rel="noopener noreferrer nofollow"
      size="sm"
      fw={600}
    >
      Открыть
    </Anchor>
  ) : null
  return (
    <Paper withBorder radius="md" p="sm">
      <Group justify="space-between" wrap="nowrap">
        <Group gap="sm" wrap="nowrap" style={{ minWidth: 0 }}>
          <Box c="blue.6">{props.icon}</Box>
          <Stack gap={0} style={{ minWidth: 0 }}>
            <Text size="sm" fw={600}>
              {props.title}
            </Text>
            <Text size="xs" c="dimmed" truncate>
              {props.hint}
            </Text>
          </Stack>
        </Group>
        {action}
      </Group>
    </Paper>
  )
}

function AddEvent({
  base,
  onAdded,
}: {
  base: string
  onAdded: (a: ApplicationDetails) => void
}) {
  const [type, setType] = useState<ApplicationEventType>('REPLY')
  const [text, setText] = useState('')
  const [at, setAt] = useState('')
  const [errors, setErrors] = useState<FieldErrors>({})

  const submit = async (e: FormEvent) => {
    e.preventDefault()
    setErrors({})
    try {
      const next = await api<ApplicationDetails>(`${base}/events`, {
        method: 'POST',
        body: { type, text, occurredAt: toApiDateTime(at) },
      })
      setText('')
      setAt('')
      onAdded(next)
    } catch (err) {
      setErrors(
        err instanceof ApiError
          ? err.errors
          : { error: 'Не удалось добавить событие' },
      )
    }
  }

  return (
    <form onSubmit={submit}>
      <Stack gap="xs" mt="md">
        <SimpleGrid cols={2}>
          <Select
            size="xs"
            data={EVENT_TYPES}
            value={type}
            onChange={(v) => v && setType(v as ApplicationEventType)}
            allowDeselect={false}
            aria-label="Тип события"
          />
          <TextInput
            size="xs"
            type="datetime-local"
            value={at}
            onChange={(e) => setAt(e.currentTarget.value)}
            aria-label="Когда"
          />
        </SimpleGrid>
        <Group gap="xs" wrap="nowrap" align="flex-start">
          <TextInput
            size="xs"
            placeholder="Например: ответили через день — назначили скрининг"
            value={text}
            onChange={(e) => setText(e.currentTarget.value)}
            error={errors.text ?? errors.error}
            style={{ flex: 1 }}
          />
          <Button
            size="xs"
            type="submit"
            variant="light"
            disabled={!text.trim()}
          >
            Добавить
          </Button>
        </Group>
      </Stack>
    </form>
  )
}

function EditForm({
  app,
  base,
  onSaved,
}: {
  app: ApplicationDetails
  base: string
  onSaved: (a: ApplicationDetails) => void
}) {
  const [form, setForm] = useState({
    company: app.company,
    position: app.position,
    salary: app.salary ?? '',
    source: app.source ?? '',
    vacancyUrl: app.vacancyUrl ?? '',
    deadline: app.deadline ?? '',
    nextStepTitle: app.nextStepTitle ?? '',
    nextStepAt: toLocalInput(app.nextStepAt),
  })
  const [errors, setErrors] = useState<FieldErrors>({})
  const [saving, setSaving] = useState(false)

  const field = (key: keyof typeof form) => ({
    value: form[key],
    onChange: (e: { currentTarget: { value: string } }) => {
      const value = e.currentTarget.value
      setForm((f) => ({ ...f, [key]: value }))
    },
    error: errors[key],
  })

  const submit = async (e: FormEvent) => {
    e.preventDefault()
    setSaving(true)
    setErrors({})
    try {
      onSaved(
        await api<ApplicationDetails>(base, {
          method: 'PUT',
          body: {
            ...form,
            resumeId: app.resume?.id ?? null,
            deadline: form.deadline || null,
            nextStepAt: toApiDateTime(form.nextStepAt),
          },
        }),
      )
    } catch (err) {
      setErrors(
        err instanceof ApiError
          ? err.errors
          : { error: 'Не удалось сохранить' },
      )
    } finally {
      setSaving(false)
    }
  }

  return (
    <form onSubmit={submit}>
      <Stack gap="xs">
        {errors.error && <Alert color="red">{errors.error}</Alert>}
        <SimpleGrid cols={2}>
          <TextInput label="Компания" required {...field('company')} />
          <TextInput label="Позиция" required {...field('position')} />
          <TextInput
            label="Вилка"
            placeholder="от 140 тыс ₽"
            {...field('salary')}
          />
          <TextInput
            label="Источник"
            placeholder="hh.ru"
            {...field('source')}
          />
        </SimpleGrid>
        <TextInput label="Ссылка на вакансию" {...field('vacancyUrl')} />
        <SimpleGrid cols={2}>
          <TextInput label="Дедлайн" type="date" {...field('deadline')} />
          <TextInput
            label="Следующий шаг: когда"
            type="datetime-local"
            {...field('nextStepAt')}
          />
        </SimpleGrid>
        <Textarea
          label="Следующий шаг"
          placeholder="Техническое интервью"
          autosize
          {...field('nextStepTitle')}
        />
        <Button type="submit" loading={saving} w="fit-content">
          Сохранить
        </Button>
      </Stack>
    </form>
  )
}
