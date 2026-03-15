export function openTelegramLink(username: string) {
  const tgUrl = `tg://resolve?domain=${username}`
  const webUrl = `https://t.me/${username}`

  const tgWindow = window.open(tgUrl, '_blank', 'noopener,noreferrer')

  setTimeout(() => {
    if (!tgWindow || tgWindow.closed) {
      window.open(webUrl, '_blank', 'noopener,noreferrer')
    }
  }, 500)
}
