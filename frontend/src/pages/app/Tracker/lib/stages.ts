import type { ApplicationStage } from '@shared/api'

export const BOARD_STAGES: ApplicationStage[] = [
  'SAVED',
  'APPLIED',
  'SCREENING',
  'INTERVIEW',
  'OFFER',
]

/** Стадии, с которых можно добавить отклик (по макету — без оффера и отказа). */
export const START_STAGES: ApplicationStage[] = [
  'SAVED',
  'APPLIED',
  'SCREENING',
  'INTERVIEW',
]

export const STAGE_LABEL: Record<ApplicationStage, string> = {
  SAVED: 'Сохранено',
  APPLIED: 'Отклик отправлен',
  SCREENING: 'Скрининг',
  INTERVIEW: 'Собеседование',
  OFFER: 'Оффер',
  REJECTED: 'Отказ',
}

/** Короткие подписи для чипов и бейджей таблицы. */
export const STAGE_SHORT: Record<ApplicationStage, string> = {
  SAVED: 'Сохранено',
  APPLIED: 'Отклик',
  SCREENING: 'Скрининг',
  INTERVIEW: 'Собес',
  OFFER: 'Оффер',
  REJECTED: 'Отказ',
}

export const STAGE_COLOR: Record<ApplicationStage, string> = {
  SAVED: 'gray',
  APPLIED: 'blue',
  SCREENING: 'orange',
  INTERVIEW: 'violet',
  OFFER: 'green',
  REJECTED: 'red',
}

/** Порядок для сортировки таблицы по этапу. */
export const STAGE_ORDER: Record<ApplicationStage, number> = {
  OFFER: 0,
  INTERVIEW: 1,
  SCREENING: 2,
  APPLIED: 3,
  SAVED: 4,
  REJECTED: 5,
}
