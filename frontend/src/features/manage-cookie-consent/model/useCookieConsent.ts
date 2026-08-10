import { useLocalStorage } from '@mantine/hooks'

import {
  defaultCookieConsentSettings,
  COOKIE_CONSENT_STORAGE_KEY,
} from './storage'

import type { CookieConsentSettings } from './types'

export const useCookieConsent = () => {
  const [settings, setSettings] = useLocalStorage<CookieConsentSettings>({
    key: COOKIE_CONSENT_STORAGE_KEY,
    defaultValue: defaultCookieConsentSettings,
  })

  const acceptAll = () => {
    setSettings({
      necessary: true,
      analytics: true,
      marketing: true,
    })
  }

  const rejectAll = () => {
    setSettings({
      necessary: true,
      analytics: false,
      marketing: false,
    })
  }

  const toggleAnalytics = () => {
    setSettings((current) => ({
      ...current,
      necessary: true,
      analytics: !current.analytics,
    }))
  }

  const toggleMarketing = () => {
    setSettings((current) => ({
      ...current,
      necessary: true,
      marketing: !current.marketing,
    }))
  }

  return {
    settings,
    setSettings,
    acceptAll,
    rejectAll,
    toggleAnalytics,
    toggleMarketing,
  }
}
