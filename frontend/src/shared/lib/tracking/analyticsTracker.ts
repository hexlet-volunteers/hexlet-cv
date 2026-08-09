import type { Tracker } from './types'

let isEnabled = false

export const analyticsTracker: Tracker = {
  enable() {
    if (typeof window === 'undefined') {
      console.warn('Аналитика вызывается не в браузере. Игнорировать')
      return
    }
    if (isEnabled === true) {
      console.log('Аналитика уже включена')
      return
    }
    console.log('Вызываем аналитику, устанавливаем занчение в true')
    isEnabled = true
  },
  disable() {
    if (typeof window === 'undefined') {
      console.warn('Отключение вызывается не в браузере. Игнорируем')
      return
    }
    if (!isEnabled) {
      console.log('Уже отключено')
      return
    }
    console.log('Отключаем аналитику, ставим маркер отключено')
    isEnabled = false
  },
}
