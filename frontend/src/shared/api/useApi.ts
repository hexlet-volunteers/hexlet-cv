import { useEffect, useState } from 'react'
import { ApiError } from './client'

interface State<T> {
  data: T | null
  error: ApiError | null
  loading: boolean
}

/**
 * Загружает данные при монтировании и при смене `deps`.
 *
 * Простая замена TanStack Query до решения по связке фронта и API (#1111):
 * без кэша, но с отменой устаревших ответов при быстрой смене параметров.
 */
export function useApi<T>(load: () => Promise<T>, deps: unknown[]): State<T> {
  const [state, setState] = useState<State<T>>({
    data: null,
    error: null,
    loading: true,
  })

  useEffect(() => {
    let active = true
    setState((s) => ({ ...s, loading: true, error: null }))
    load()
      .then((data) => active && setState({ data, error: null, loading: false }))
      .catch((error: unknown) => {
        if (!active) return
        const apiError =
          error instanceof ApiError
            ? error
            : new ApiError(0, { error: 'Сеть недоступна' })
        setState({ data: null, error: apiError, loading: false })
      })
    return () => {
      active = false
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps)

  return state
}
