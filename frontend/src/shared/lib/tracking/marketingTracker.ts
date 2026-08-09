import type { Tracker } from './types'

let isEnabled = false

export const marketingTracker: Tracker = {
  enable() {
    if (typeof window === 'undefined') {
      console.warn('Маркетинг-тракер вызывается не в браузере. Игнорировать')
      return
    }
    if (isEnabled === true) {
      console.log('Маркетинг-трекер уже включен')
      return
    }
    console.log('Вызываем маркетинг-тракер, устанавливаем занчение в true')
    isEnabled = true
  },
  disable() {
    if (typeof window === 'undefined') {
      console.warn('Маркетинг-тракер вызывается не в браузере. Игнорируем')
      return
    }
    if (!isEnabled) {
      console.log('Маркетинг-тракер. Уже отключено')
      return
    }
    console.log('Отключаем маркетинг-тракер, ставим маркер отключено')
    isEnabled = false
  },
}
