/** Глобальный интерфейс функции инициализации и отправки целей Яндекс.Метрики. */
declare global {
  interface Window {
    ym?: (method: string, counterId: number, params?: object) => void
  }
}

/**
 * Инициализирует и загружает скрипт счетчика Яндекс.Метрики в DOM-дерево.
 *
 * @returns {void}
 */

const loadYandexMetrica = (): void => {
  if (typeof window === 'undefined') {
    return
  }

  if (window.ym) {
    return
  }

  console.log('Яндекс.Метрика успешно загружена!')
  // Тут будет стандартный код инициализации счетчика Яндекса
}

export default loadYandexMetrica
