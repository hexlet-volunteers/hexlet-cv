import type { Author } from '@shared/api'

export function fullName(a: Pick<Author, 'firstName' | 'lastName'>) {
  return [a.firstName, a.lastName].filter(Boolean).join(' ') || 'Без имени'
}

const dateFormat = new Intl.DateTimeFormat('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' })
const monthFormat = new Intl.DateTimeFormat('ru-RU', { month: 'long', year: 'numeric' })

export function formatDate(iso: string | null) {
  return iso ? dateFormat.format(new Date(iso)) : ''
}

export function period(begin: string | null, end: string | null, current: boolean | null) {
  const from = begin ? monthFormat.format(new Date(begin)) : ''
  const to = current ? 'по настоящее время' : end ? monthFormat.format(new Date(end)) : ''
  return [from, to].filter(Boolean).join(' — ')
}
