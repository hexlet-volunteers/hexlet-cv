import * as cookie from 'cookie'

export type CookieOptions = cookie.SerializeOptions

export const parseCookies = (header: string | null) => {
  return cookie.parse(header ?? '')
}

export const serializeCookies = (name: string, value: string, options: CookieOptions) => {
  return cookie.serialize(name, value, options)
}
