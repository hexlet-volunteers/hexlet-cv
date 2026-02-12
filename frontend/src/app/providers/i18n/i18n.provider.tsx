import i18next from 'i18next'
import { I18nextProvider } from 'react-i18next'
import { useMemo } from 'react'
import resources from './locales'

type Props = {
  children: React.ReactNode
  locale?: string
}

const I18nProvider: React.FC<Props> = ({ children, locale }) => {
  const i18nextInstance = useMemo(() => {
    const instance = i18next.createInstance()
    void instance.init({
      debug: false,
      lng: locale ?? 'ru',
      resources,
      interpolation: {
        escapeValue: false,
      },
    })

    return instance
  }, [locale])

  return (
    <I18nextProvider i18n={i18nextInstance}>
      { children }
    </I18nextProvider>
  )
}

export default I18nProvider
