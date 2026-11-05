import type { ApplicationSummary } from '@shared/api'

/** `YYYY-MM-DD HH:mm` от бэкенда → Date. */
export const parse = (value: string) =>
  new Date(value.includes(' ') ? value.replace(' ', 'T') : value)

/** Значение `datetime-local` → формат бэкенда `YYYY-MM-DD HH:mm`. */
export const toApiDateTime = (local: string) =>
  local ? local.replace('T', ' ').slice(0, 16) : null

/** Формат бэкенда → значение `datetime-local`. */
export const toLocalInput = (api?: string) =>
  api ? api.replace(' ', 'T').slice(0, 16) : ''

export function plural(n: number, one: string, few: string, many: string) {
  const mod10 = n % 10
  const mod100 = n % 100
  if (mod10 === 1 && mod100 !== 11) return one
  if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) return few
  return many
}

const startOfDay = (d: Date) =>
  new Date(d.getFullYear(), d.getMonth(), d.getDate())
const dayDiff = (a: Date, b: Date) =>
  Math.round((startOfDay(a).getTime() - startOfDay(b).getTime()) / 86_400_000)

/** «сегодня», «вчера», «3 дня назад». */
export function ago(value?: string) {
  if (!value) return ''
  const days = dayDiff(new Date(), parse(value))
  if (days <= 0) return 'сегодня'
  if (days === 1) return 'вчера'
  return `${days} ${plural(days, 'день', 'дня', 'дней')} назад`
}

const time = new Intl.DateTimeFormat('ru-RU', {
  hour: '2-digit',
  minute: '2-digit',
})
const dayMonth = new Intl.DateTimeFormat('ru-RU', {
  day: 'numeric',
  month: 'long',
})
const dayShort = new Intl.DateTimeFormat('ru-RU', {
  day: 'numeric',
  month: 'short',
})
const weekday = new Intl.DateTimeFormat('ru-RU', { weekday: 'long' })

/** «завтра 11:00», «пятница 10 июля, 14:00». */
export function when(value: string) {
  const d = parse(value)
  const days = dayDiff(d, new Date())
  const t = time.format(d)
  if (days === 0) return `сегодня ${t}`
  if (days === 1) return `завтра ${t}`
  if (days > 1 && days < 7)
    return `${weekday.format(d)} ${dayMonth.format(d)}, ${t}`
  return `${dayMonth.format(d)}, ${t}`
}

/** «20 июн» для таймлайна. */
export const shortDate = (value: string) =>
  dayShort.format(parse(value)).replace('.', '')

export const dateLong = (value: string) => dayMonth.format(parse(value))

/** Тексты подсказок из макета. */
export function alertTexts(a: ApplicationSummary) {
  return a.alerts.map((alert) => {
    if (alert.type === 'SILENCE') {
      return `${alert.days} ${plural(alert.days, 'день', 'дня', 'дней')} тишины — фоллоу-ап?`
    }
    if (a.stage === 'OFFER' && a.deadline)
      return `ответить до ${dateLong(a.deadline)}`
    if (alert.days === 0) return 'закрывается сегодня'
    return `закрывается через ${alert.days} ${plural(alert.days, 'день', 'дня', 'дней')}`
  })
}

/** Следующий шаг одной строкой: «завтра 11:00 · техническое». */
export function nextStep(a: ApplicationSummary) {
  if (a.nextStepAt)
    return [when(a.nextStepAt), a.nextStepTitle].filter(Boolean).join(' · ')
  return a.nextStepTitle ?? ''
}

/** Источник по ссылке на вакансию — только по домену, без разбора страницы. */
export function sourceFromUrl(url: string) {
  try {
    const host = new URL(url).hostname.replace(/^www\./, '')
    if (host.endsWith('hh.ru')) return 'hh.ru'
    if (host === 'career.habr.com') return 'Хабр Карьера'
    if (host === 't.me' || host === 'telegram.me') return 'Telegram'
    if (host.endsWith('getmatch.ru')) return 'Getmatch'
    if (host.endsWith('linkedin.com')) return 'LinkedIn'
    return host
  } catch {
    return undefined
  }
}

const AVATAR_COLORS = [
  'blue',
  'orange',
  'violet',
  'teal',
  'pink',
  'grape',
  'cyan',
  'green',
]

export function avatarColor(company: string) {
  let hash = 0
  for (const ch of company) hash = (hash * 31 + ch.charCodeAt(0)) >>> 0
  return AVATAR_COLORS[hash % AVATAR_COLORS.length]
}
