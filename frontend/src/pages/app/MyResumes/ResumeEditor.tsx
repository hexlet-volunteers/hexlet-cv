import { useEffect, useState, type FormEvent } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import {
  Alert,
  Anchor,
  Button,
  Card,
  Center,
  Group,
  Loader,
  SimpleGrid,
  Stack,
  Switch,
  Text,
  TextInput,
  Textarea,
  Title,
} from '@mantine/core'
import { RequireAuth } from '@features/auth'
import {
  api,
  ApiError,
  type FieldErrors,
  type OwnResume,
  type ResumeForm,
  type WorkForm,
  type EducationForm,
} from '@shared/api'
import { ExperienceFields } from './ui/ExperienceFields'

const empty: ResumeForm = {
  name: '',
  summary: '',
  skillsDescription: '',
  projectsDescription: '',
  awardsDescription: '',
  aboutMyself: '',
  githubUrl: '',
  city: '',
  relocation: '',
  englishFluency: '',
  contactEmail: '',
  contactTelegram: '',
  contactPhone: '',
  published: false,
  works: [],
  educations: [],
}

const emptyWork: WorkForm = {
  company: '',
  position: '',
  beginDate: '',
  endDate: '',
  current: false,
  description: '',
}

const emptyEducation: EducationForm = {
  institution: '',
  faculty: '',
  beginDate: '',
  endDate: '',
  current: false,
  description: '',
}

/** null из API превращаем в пустые строки, чтобы поля формы оставались управляемыми. */
const clean = <T extends object>(base: T, value: object): T => {
  const result = { ...base }
  for (const key of Object.keys(base) as (keyof T)[]) {
    const v = (value as T)[key]
    if (v !== null && v !== undefined) result[key] = v
  }
  return result
}

/** Пустые даты не отправляем: бэкенд ждёт либо дату, либо null. */
const toPayload = (form: ResumeForm) => ({
  ...form,
  works: form.works.map((w) => ({
    ...w,
    beginDate: w.beginDate || null,
    endDate: w.endDate || null,
  })),
  educations: form.educations.map((e) => ({
    ...e,
    beginDate: e.beginDate || null,
    endDate: e.endDate || null,
  })),
})

function fromResume(r: OwnResume): ResumeForm {
  const { works = [], educations = [], ...rest } = r
  return {
    ...clean({ ...empty, works: [], educations: [] }, rest),
    works: works.map((w) => clean(emptyWork, w)),
    educations: educations.map((e) => clean(emptyEducation, e)),
  }
}

function Editor() {
  const { id } = useParams()
  const isNew = id === undefined
  const navigate = useNavigate()

  const [form, setForm] = useState<ResumeForm>(empty)
  const [loaded, setLoaded] = useState(isNew)
  const [loadError, setLoadError] = useState(false)
  const [errors, setErrors] = useState<FieldErrors>({})
  const [saving, setSaving] = useState(false)
  const [saved, setSaved] = useState(false)

  useEffect(() => {
    if (isNew) return
    api<OwnResume>(`/api/account/resumes/${id}`)
      .then((r) => {
        setForm(fromResume(r))
        setLoaded(true)
      })
      .catch(() => setLoadError(true))
  }, [id, isNew])

  const text = (field: keyof ResumeForm) => ({
    value: form[field] as string,
    onChange: (e: { currentTarget: { value: string } }) => {
      const value = e.currentTarget.value
      setForm((f) => ({ ...f, [field]: value }))
    },
    error: errors[field],
  })

  const submit = async (e: FormEvent) => {
    e.preventDefault()
    setSaving(true)
    setSaved(false)
    setErrors({})
    try {
      const result = await api<OwnResume>(
        isNew ? '/api/account/resumes' : `/api/account/resumes/${id}`,
        {
          method: isNew ? 'POST' : 'PUT',
          body: toPayload(form),
        },
      )
      setSaved(true)
      if (isNew) navigate(`/app/resumes/${result.id}`, { replace: true })
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

  if (loadError) return <Alert color="red">Резюме не найдено.</Alert>
  if (!loaded) {
    return (
      <Center py={80}>
        <Loader />
      </Center>
    )
  }

  return (
    <form onSubmit={submit} noValidate>
      <Stack maw={860} gap="lg">
        <div>
          <Anchor component={Link} to="/app/resumes" fz="sm" c="dimmed">
            ← Мои резюме
          </Anchor>
          <Title order={1} fz={28} mt={4}>
            {isNew ? 'Новое резюме' : form.name || 'Резюме'}
          </Title>
        </div>

        {errors.error && <Alert color="red">{errors.error}</Alert>}
        {saved && <Alert color="green">Резюме сохранено</Alert>}

        <Card withBorder radius="md" p="lg">
          <Stack>
            <TextInput
              label="Название резюме"
              placeholder="Junior Java-разработчик"
              required
              {...text('name')}
            />
            <Textarea
              label="Коротко о себе"
              autosize
              minRows={3}
              {...text('summary')}
            />
            <Textarea
              label="Навыки"
              description="Языки, фреймворки, инструменты"
              autosize
              minRows={3}
              {...text('skillsDescription')}
            />
            <Textarea
              label="Проекты"
              autosize
              minRows={3}
              {...text('projectsDescription')}
            />
            <Textarea
              label="Достижения"
              autosize
              minRows={2}
              {...text('awardsDescription')}
            />
            <Textarea
              label="Дополнительно"
              autosize
              minRows={2}
              {...text('aboutMyself')}
            />
          </Stack>
        </Card>

        <ExperienceFields
          title="Опыт работы"
          addLabel="Добавить место работы"
          name="works"
          items={form.works}
          empty={emptyWork}
          errors={errors}
          fields={[
            { key: 'company', label: 'Компания', required: true },
            {
              key: 'position',
              label: 'Должность',
              placeholder: 'Junior-разработчик',
            },
          ]}
          onChange={(works) => setForm((f) => ({ ...f, works }))}
        />

        <ExperienceFields
          title="Образование"
          addLabel="Добавить учёбу"
          name="educations"
          items={form.educations}
          empty={emptyEducation}
          errors={errors}
          fields={[
            { key: 'institution', label: 'Учебное заведение', required: true },
            { key: 'faculty', label: 'Факультет или курс' },
          ]}
          onChange={(educations) => setForm((f) => ({ ...f, educations }))}
        />

        <Card withBorder radius="md" p="lg">
          <SimpleGrid cols={{ base: 1, sm: 2 }}>
            <TextInput label="Город" {...text('city')} />
            <TextInput
              label="Переезд"
              placeholder="Готов, не готов, удалённо"
              {...text('relocation')}
            />
            <TextInput
              label="Английский"
              placeholder="B1"
              {...text('englishFluency')}
            />
            <TextInput
              label="GitHub"
              placeholder="https://github.com/…"
              {...text('githubUrl')}
            />
          </SimpleGrid>
        </Card>

        <Card withBorder radius="md" p="lg">
          <Text fw={700}>Контакты</Text>
          <Text fz="sm" c="dimmed" mb="sm">
            Email и телефон видите только вы. Telegram показывается на публичной
            странице.
          </Text>
          <SimpleGrid cols={{ base: 1, sm: 3 }}>
            <TextInput
              label="Telegram"
              placeholder="@username"
              {...text('contactTelegram')}
            />
            <TextInput label="Email" type="email" {...text('contactEmail')} />
            <TextInput label="Телефон" type="tel" {...text('contactPhone')} />
          </SimpleGrid>
        </Card>

        <Group justify="space-between">
          <Switch
            label="Опубликовать в каталоге"
            checked={form.published}
            onChange={(e) => {
              const checked = e.currentTarget.checked
              setForm((f) => ({ ...f, published: checked }))
            }}
          />
          <Group>
            {!isNew && form.published && (
              <Button component={Link} to={`/resumes/${id}`} variant="subtle">
                Открыть публичную страницу
              </Button>
            )}
            <Button type="submit" loading={saving}>
              Сохранить
            </Button>
          </Group>
        </Group>
      </Stack>
    </form>
  )
}

/** Создание и редактирование своего резюме (#1309). */
export function ResumeEditor() {
  return (
    <RequireAuth>
      <Editor />
    </RequireAuth>
  )
}
