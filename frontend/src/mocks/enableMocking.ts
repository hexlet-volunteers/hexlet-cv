export async function enableMocking() {
  if (!import.meta.env.DEV) return
  if (import.meta.env.VITE_MSW !== 'true') return

  const { worker } = await import('./browser')

  await worker.start({
    onUnhandledRequest: 'error',
  })
}
