/**
 * Инициализирует и загружает скрипт маркетингового трекера.
 *
 * @returns {void}
 */

const loadMarketingScripts = (): void => {
  if (typeof window === 'undefined') {
    return
  }

  // if (window.vkPrice) return; или любой другой маркетирг трекер

  console.log('Маркетинговые трекеры успешно загружены!')

  // Здесь будет код Пикселя ВКонтакте или Google Ads:
}

export default loadMarketingScripts
