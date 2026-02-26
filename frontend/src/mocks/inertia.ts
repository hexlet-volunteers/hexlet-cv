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
  init?: { status?: number, headers?: Record<string, string> }
) {
  return HttpResponse.json(page, {
    status: init?.status ?? 200,
    headers: {
      'X-Inertia': 'true',
      'Vary': 'X-Inertia',
      ...init?.headers,
    },
  })
}

// Inertia navigation: 409 + X-Inertia-Location
export const inertiaRedirect = (to: string) =>
  new HttpResponse(null, {
    status: 409,
    headers: {
      'X-Inertia-Location': to,
    },
  })

// обычная навигация: 303 Location
export const classicRedirect = (to: string) =>
  new HttpResponse(null, {
    status: 303,
    headers: { Location: to },
  })

export const isInertia = (req: Request) => req.headers.get('X-Inertia') === 'true'
