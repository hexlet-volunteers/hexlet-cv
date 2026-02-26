import { http, HttpResponse, type DefaultBodyType } from 'msw'
import { createCtx } from '@mocks/msw/createCtx'
import { runPipeline } from '@mocks/msw/pipeline'
import { authMiddleware } from '@mocks/middleware/auth'

const middlewares = [authMiddleware]

type GetHandler = (ctx: ReturnType<typeof createCtx>, request: Request) => HttpResponse<DefaultBodyType>
type PostHandler<TBody> = (ctx: ReturnType<typeof createCtx>, request: Request, body: TBody) => HttpResponse<DefaultBodyType>

async function readBody<TBody>(request: Request): Promise<TBody> {
  const contentType = (request.headers.get('content-type') ?? '').toLowerCase()

  if (contentType.includes('application/json')) {
    try {
      return (await request.json()) as TBody
    // eslint-disable-next-line @stylistic/brace-style
    } catch {
      return {} as TBody
    }
  }

  if (contentType.includes('application/x-www-form-urlencoded')) {
    const text = await request.text()
    return Object.fromEntries(new URLSearchParams(text).entries()) as TBody
  }

  if (contentType.includes('multipart/form-data')) {
    const fd = await request.formData()
    const out: Record<string, FormDataEntryValue> = {}
    fd.forEach((v, k) => {
      out[k] = v
    })
    return out as TBody
  }

  const text = await request.text()

  return Object.fromEntries(new URLSearchParams(text).entries()) as TBody
}

export const defineGet = (path: string, handler: GetHandler) => {
  return http.get(path, async ({ request, cookies }) => {
    const ctx = createCtx(request, { cookies })
    const piped = runPipeline(middlewares, (c, r) => {
      console.log('MSW handler hit:', request.method, request.url)
      return handler(c, r)
    })
    return piped(ctx, request)
  })
}

export const definePost = <TBody>(path: string, handler: PostHandler<TBody>) => {
  return http.post(path, async ({ request, cookies }) => {
    const ctx = createCtx(request, { cookies })
    const body = await readBody<TBody>(request)
    const piped = runPipeline(middlewares, (c, r) => {
      console.log('MSW handler hit:', request.method, request.url)
      return handler(c, r, body)
    })
    return piped(ctx, request)
  })
}
