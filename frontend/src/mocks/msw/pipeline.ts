import type { DefaultBodyType, HttpResponse } from 'msw'
import type { MswCtx } from '@mocks/msw/createCtx'

export type Middleware = (
  ctx: MswCtx,
  request: Request
) => HttpResponse<DefaultBodyType> | undefined

export const runPipeline
  = (middlewares: Middleware[], handler: Middleware): Middleware =>
    (ctx, request) => {
      for (const mw of middlewares) {
        const res = mw(ctx, request)
        if (res) return res
      }
      return handler(ctx, request)
    }
