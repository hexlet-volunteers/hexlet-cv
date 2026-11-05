/**
 * Клиент REST API бэкенда (`/api/**`).
 *
 * Токены живут в HttpOnly-куках, поэтому фронт их не видит: запросы идут с
 * `credentials: 'include'`, а при 401 клиент один раз просит новый access-токен
 * через `/api/auth/refresh` и повторяет запрос.
 */

/** Ошибки бэкенда приходят как `{ errors: { поле: сообщение } }`. */
export type FieldErrors = Record<string, string>

export class ApiError extends Error {
  readonly status: number
  readonly errors: FieldErrors

  constructor(status: number, errors: FieldErrors) {
    super(Object.values(errors)[0] ?? `HTTP ${status}`)
    this.status = status
    this.errors = errors
  }
}

type Method = 'GET' | 'POST' | 'PUT' | 'DELETE'

async function send(method: Method, path: string, body?: unknown) {
  return fetch(path, {
    method,
    credentials: 'include',
    headers: body === undefined ? {} : { 'Content-Type': 'application/json' },
    body: body === undefined ? undefined : JSON.stringify(body),
  })
}

let refreshing: Promise<boolean> | null = null

/** Один запрос на обновление токена, даже если 401 пришёл сразу нескольким вызовам. */
function refresh(): Promise<boolean> {
  refreshing ??= send('POST', '/api/auth/refresh')
    .then((r) => r.ok)
    .catch(() => false)
    .finally(() => {
      refreshing = null
    })
  return refreshing
}

const NO_RETRY = ['/api/auth/login', '/api/auth/register', '/api/auth/refresh']

export async function api<T>(
  path: string,
  options: { method?: Method; body?: unknown } = {},
): Promise<T> {
  const method = options.method ?? 'GET'
  let response = await send(method, path, options.body)

  if (
    response.status === 401 &&
    !NO_RETRY.includes(path) &&
    (await refresh())
  ) {
    response = await send(method, path, options.body)
  }

  if (!response.ok) {
    let errors: FieldErrors = {}
    try {
      const data = (await response.json()) as { errors?: FieldErrors }
      errors = data.errors ?? {}
    } catch {
      // тело пустое или не JSON — оставляем общий текст ошибки
    }
    throw new ApiError(response.status, errors)
  }

  if (response.status === 204) {
    return undefined as T
  }
  return (await response.json()) as T
}
