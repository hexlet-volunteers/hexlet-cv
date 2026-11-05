import { useState, type FormEvent } from 'react'
import {
  Alert,
  Button,
  Chip,
  Group,
  Modal,
  Select,
  SimpleGrid,
  Stack,
  Text,
  TextInput,
} from '@mantine/core'
import {
  api,
  ApiError,
  type ApplicationDetails,
  type ApplicationStage,
  type FieldErrors,
} from '@shared/api'
import { sourceFromUrl } from '../lib/format'
import { START_STAGES, STAGE_LABEL } from '../lib/stages'

interface Props {
  opened: boolean
  onClose: () => void
  resumes: { id: number; name: string }[]
  onCreated: (a: ApplicationDetails) => void
}

const empty = {
  vacancyUrl: '',
  company: '',
  position: '',
  stage: 'SAVED' as ApplicationStage,
  resumeId: '',
}

/**
 * «Новый отклик» (#1159). Автозаполнение компании и роли по ссылке — задача парсера
 * вакансий (#1147); пока ссылка сохраняется в отклике, а источник определяется по домену.
 */
export function AddApplicationModal({
  opened,
  onClose,
  resumes,
  onCreated,
}: Props) {
  const [form, setForm] = useState(empty)
  const [errors, setErrors] = useState<FieldErrors>({})
  const [saving, setSaving] = useState(false)

  const close = () => {
    setForm(empty)
    setErrors({})
    onClose()
  }

  const submit = async (e: FormEvent) => {
    e.preventDefault()
    const local: FieldErrors = {}
    if (!form.company.trim()) local.company = 'Укажите компанию'
    if (!form.position.trim()) local.position = 'Укажите позицию'
    if (Object.keys(local).length) {
      setErrors(local)
      return
    }
    setSaving(true)
    setErrors({})
    try {
      const created = await api<ApplicationDetails>(
        '/api/account/applications',
        {
          method: 'POST',
          body: {
            company: form.company,
            position: form.position,
            vacancyUrl: form.vacancyUrl.trim(),
            source: form.vacancyUrl
              ? sourceFromUrl(form.vacancyUrl.trim())
              : undefined,
            stage: form.stage,
            resumeId: form.resumeId ? Number(form.resumeId) : null,
          },
        },
      )
      onCreated(created)
      close()
    } catch (err) {
      setErrors(
        err instanceof ApiError
          ? err.errors
          : { error: 'Не удалось добавить отклик' },
      )
    } finally {
      setSaving(false)
    }
  }

  const set = (key: 'vacancyUrl' | 'company' | 'position') => ({
    value: form[key],
    onChange: (e: { currentTarget: { value: string } }) => {
      const value = e.currentTarget.value
      setForm((f) => ({ ...f, [key]: value }))
    },
    error: errors[key],
  })

  return (
    <Modal
      opened={opened}
      onClose={close}
      title={
        <Text fw={800} fz="xl">
          Новый отклик
        </Text>
      }
      size="lg"
      radius="lg"
    >
      <form onSubmit={submit} noValidate>
        <Stack>
          {errors.error && <Alert color="red">{errors.error}</Alert>}
          <TextInput
            label="Ссылка на вакансию"
            placeholder="hh.ru/vacancy/… или ссылка из Telegram"
            description="Сохраним ссылку в отклике. Компанию и роль пока заполните вручную"
            inputWrapperOrder={['label', 'input', 'description', 'error']}
            {...set('vacancyUrl')}
          />
          <SimpleGrid cols={{ base: 1, sm: 2 }}>
            <TextInput
              label="Компания"
              placeholder="Например, Циан"
              required
              {...set('company')}
            />
            <TextInput
              label="Позиция"
              placeholder="Junior Frontend"
              required
              {...set('position')}
            />
          </SimpleGrid>
          <div>
            <Text size="sm" fw={500} mb={6}>
              Этап
            </Text>
            <Chip.Group
              value={form.stage}
              onChange={(v) =>
                setForm((f) => ({ ...f, stage: v as ApplicationStage }))
              }
            >
              <Group gap="xs">
                {START_STAGES.map((s) => (
                  <Chip key={s} value={s} variant="outline" color="dark">
                    {STAGE_LABEL[s]}
                  </Chip>
                ))}
              </Group>
            </Chip.Group>
          </div>
          <Select
            label="Резюме"
            placeholder={
              resumes.length
                ? 'Какая версия ушла в отклик'
                : 'Сначала создайте резюме'
            }
            data={resumes.map((r) => ({ value: String(r.id), label: r.name }))}
            value={form.resumeId || null}
            onChange={(v) => setForm((f) => ({ ...f, resumeId: v ?? '' }))}
            error={errors.resumeId}
            clearable
            disabled={resumes.length === 0}
          />
          <Group justify="flex-end">
            <Button variant="default" onClick={close}>
              Отмена
            </Button>
            <Button type="submit" loading={saving}>
              Добавить в воронку
            </Button>
          </Group>
        </Stack>
      </form>
    </Modal>
  )
}
