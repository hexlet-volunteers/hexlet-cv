import { HttpResponse } from 'msw'

export type InertiaPage = {
  component: string
  props: Record<string, unknown>
  url: string
  version?: string | number
  clearHistory?: boolean
  encryptHistory?: boolean
  mergeProps?: string[]
  prependProps?: string[]
  deepMergeProps?: string[]
}

export function inertiaJson(
  page: InertiaPage,
  init?: { status?: number; headers?: Record<string, string> },
) {
  return HttpResponse.json(page, {
    status: init?.status ?? 200,
    headers: {
      'X-Inertia': 'true',
      Vary: 'X-Inertia',
      ...init?.headers,
    },
  })
}
