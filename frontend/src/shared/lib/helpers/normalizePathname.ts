export function normalizePathname(pathname?: string) {
  if (!pathname) return

  let normalized = pathname

  normalized = normalized.replace(/^\/[a-z]{2}\//, '/')

  if (pathname.length > 1 && pathname.startsWith('/'))
    normalized = normalized.slice(1)
  if (pathname.length > 1 && pathname.endsWith('/'))
    normalized = normalized.slice(0, -1)

  return normalized
}
