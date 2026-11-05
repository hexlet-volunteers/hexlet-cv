import {
  ActionIcon,
  Button,
  Card,
  Checkbox,
  Group,
  SimpleGrid,
  Stack,
  Text,
  TextInput,
  Textarea,
} from '@mantine/core'
import { IconPlus, IconTrash } from '@tabler/icons-react'
import type { EducationForm, FieldErrors, WorkForm } from '@shared/api'

/** `YYYY-MM-DD` из API ↔ `YYYY-MM` для поля «месяц». */
const toMonth = (date: string) => date.slice(0, 7)
const fromMonth = (month: string) => (month ? `${month}-01` : '')

interface Field<T> {
  key: keyof T & string
  label: string
  placeholder?: string
  required?: boolean
}

interface Props<T extends WorkForm | EducationForm> {
  title: string
  addLabel: string
  /** Имя списка в ошибках бэкенда: `works[0].company`. */
  name: 'works' | 'educations'
  items: T[]
  fields: [Field<T>, Field<T>]
  empty: T
  errors: FieldErrors
  onChange: (items: T[]) => void
}

/** Повторяющийся блок «место работы / учёбы» с добавлением и удалением записей. */
export function ExperienceFields<T extends WorkForm | EducationForm>({
  title,
  addLabel,
  name,
  items,
  fields,
  empty,
  errors,
  onChange,
}: Props<T>) {
  const update = (index: number, patch: Partial<T>) =>
    onChange(
      items.map((item, i) => (i === index ? { ...item, ...patch } : item)),
    )

  return (
    <Card withBorder radius="md" p="lg">
      <Group justify="space-between" mb="sm">
        <Text fw={700}>{title}</Text>
        <Button
          variant="light"
          size="xs"
          leftSection={<IconPlus size={14} />}
          onClick={() => onChange([...items, { ...empty }])}
        >
          {addLabel}
        </Button>
      </Group>

      {items.length === 0 && (
        <Text fz="sm" c="dimmed">
          Пока ничего не добавлено.
        </Text>
      )}

      <Stack gap="lg">
        {items.map((item, index) => {
          const err = (field: string) => errors[`${name}[${index}].${field}`]
          return (
            <Stack
              key={index}
              gap="xs"
              pt={index ? 'md' : 0}
              style={
                index
                  ? { borderTop: '1px solid var(--mantine-color-gray-2)' }
                  : undefined
              }
            >
              <Group align="flex-start" wrap="nowrap">
                <SimpleGrid cols={{ base: 1, sm: 2 }} style={{ flex: 1 }}>
                  {fields.map((f) => (
                    <TextInput
                      key={f.key}
                      label={f.label}
                      placeholder={f.placeholder}
                      required={f.required}
                      value={item[f.key] as string}
                      error={err(f.key)}
                      onChange={(e) => {
                        const value = e.currentTarget.value
                        update(index, { [f.key]: value } as Partial<T>)
                      }}
                    />
                  ))}
                </SimpleGrid>
                <ActionIcon
                  variant="subtle"
                  color="gray"
                  mt={26}
                  aria-label="Удалить"
                  onClick={() => onChange(items.filter((_, i) => i !== index))}
                >
                  <IconTrash size={16} />
                </ActionIcon>
              </Group>
              <Group align="flex-end">
                <TextInput
                  type="month"
                  label="Начало"
                  value={toMonth(item.beginDate)}
                  onChange={(e) => {
                    const value = e.currentTarget.value
                    update(index, { beginDate: fromMonth(value) } as Partial<T>)
                  }}
                  w={170}
                />
                <TextInput
                  type="month"
                  label="Окончание"
                  value={toMonth(item.endDate)}
                  disabled={item.current}
                  onChange={(e) => {
                    const value = e.currentTarget.value
                    update(index, { endDate: fromMonth(value) } as Partial<T>)
                  }}
                  w={170}
                />
                <Checkbox
                  label="По настоящее время"
                  checked={item.current}
                  mb={8}
                  onChange={(e) => {
                    const checked = e.currentTarget.checked
                    update(index, {
                      current: checked,
                      endDate: checked ? '' : item.endDate,
                    } as Partial<T>)
                  }}
                />
              </Group>
              <Textarea
                label="Описание"
                description="До 255 символов"
                maxLength={255}
                autosize
                minRows={2}
                value={item.description}
                error={err('description')}
                onChange={(e) => {
                  const value = e.currentTarget.value
                  update(index, { description: value } as Partial<T>)
                }}
              />
            </Stack>
          )
        })}
      </Stack>
    </Card>
  )
}
