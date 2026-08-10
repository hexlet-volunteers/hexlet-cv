import type { CookieConsentSettings } from './types'

/**
 * Ключ localStorage и дефолтные настройки согласия на cookies
 */

export const COOKIE_CONSENT_STORAGE_KEY = 'cookie-consent'

export const defaultCookieConsentSettings: CookieConsentSettings = {
  necessary: true,
  analytics: false,
  marketing: false,
}
