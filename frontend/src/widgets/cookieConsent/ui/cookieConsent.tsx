import { useEffect, useState } from 'react'
import { useLocalStorage } from '@mantine/hooks'
import { loadYandexMetrica, loadMarketingScripts } from '@shared/lib'
import { Button, Paper, Text, Group, Checkbox, Stack } from '@mantine/core'

/**
 * Структура объекта настроек согласия пользователя на использование файлов cookie.
 */
interface CookieConsentSettings {
  necessary: boolean
  analytics: boolean
  marketing: boolean
}

/**
 * Виджет согласия на использование файлов cookie согласно требованиям 152-ФЗ.
 * раздельный выбор необязательных категорий
 * блокирует запуск трекеров до явного согласия.
 *
 * @component
 * @returns {JSX.Element | null} Возвращает разметку баннера или null, если выбор уже сделан.
 */

export const CookieConsent = () => {
  const [consent, setConsent] = useLocalStorage<CookieConsentSettings | null>({
    key: 'cookieConsent',
    defaultValue: null,
  })
  const [isVisible, setIsVisible] = useState(false)
  const [analyticsChecked, setAnalyticsChecked] = useState(false)
  const [marketingChecked, setMarketingChecked] = useState(false)

  useEffect(() => {
    if (consent === null) {
      setIsVisible(true)
    } else {
      if (consent.analytics) {
        loadYandexMetrica()
      }
      if (consent.marketing) {
        loadMarketingScripts()
      }
    }
  }, [consent])

  const handleAcceptSettings = () => {
    const settings = {
      necessary: true,
      analytics: analyticsChecked,
      marketing: marketingChecked,
    }

    setConsent(settings)
    setIsVisible(false)

    if (analyticsChecked) {
      loadYandexMetrica()
    }
    if (marketingChecked) {
      loadMarketingScripts()
    }
  }

  const handleAcceptAll = () => {
    const settings: CookieConsentSettings = {
      necessary: true,
      analytics: true,
      marketing: true,
    }

    setConsent(settings)
    setIsVisible(false)
    loadYandexMetrica()
    loadMarketingScripts()
  }

  const handleRejectAll = () => {
    const settings: CookieConsentSettings = {
      necessary: true,
      analytics: false,
      marketing: false,
    }

    setConsent(settings)
    setIsVisible(false)
  }

  if (!isVisible || consent !== null) {
    return null
  }

  return (
    <Paper
      shadow="xl"
      withBorder
      p="xl"
      style={{
        position: 'fixed',
        top: 20,
        bottom: 20,
        right: 20,
        zIndex: 1000,
        maxWidth: 400,
      }}
    >
      <Text size="sm" fw="bold" mb="md">
        Управление cookie
      </Text>
      <Text size="sm" ta="justify">
        Здесь вы можете изменить своё согласие на использование файлов cookie.
        Мы собираем только те данные, которые помогают делать сайт лучше, и не
        передаём вашу личную информацию без вашего ведома.
      </Text>
      <Stack
        h={250}
        bg="var(--mantine-color-body)"
        align="stretch"
        justify="center"
        gap="xs"
      >
        <Checkbox
          checked={true}
          disabled
          label="Необходимые (технические) cookie"
          description="Гарантируют стабильную и безопасную работу сайта."
          size="sm"
        />
        <Checkbox
          checked={analyticsChecked}
          onChange={(event) => setAnalyticsChecked(event.currentTarget.checked)}
          label="Аналитические cookie"
          description="Помогают нам понимать, как пользователи взаимодействуют с сайтом."
          size="sm"
        />
        <Checkbox
          checked={marketingChecked}
          onChange={(event) => setMarketingChecked(event.currentTarget.checked)}
          label="Маркетинговые cookie"
          description="Показывают вам релевантные предложения и
        учитывают ваши интересы."
          size="sm"
        />
      </Stack>
      <Group>
        <Button variant="filled" onClick={handleAcceptAll}>
          Принять все
        </Button>
        <Button variant="filled" onClick={handleAcceptSettings}>
          Принять
        </Button>
        <Button
          variant="filled"
          onClick={handleRejectAll}
          color="rgba(176, 223, 255, 1)"
        >
          Отклонить
        </Button>
      </Group>
    </Paper>
  )
}
