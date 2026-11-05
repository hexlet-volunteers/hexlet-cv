import { Button } from '@mantine/core'
import { IconBrandGithub } from '@tabler/icons-react'

/**
 * Вход через GitHub. Обычная ссылка, а не fetch: OAuth уводит браузер на github.com и обратно,
 * бэкенд ставит куки и редиректит в кабинет.
 */
export function GithubButton({
  label = 'Войти через GitHub',
}: {
  label?: string
}) {
  return (
    <Button
      component="a"
      href="/oauth2/authorization/github"
      variant="default"
      fullWidth
      leftSection={<IconBrandGithub size={18} />}
    >
      {label}
    </Button>
  )
}
