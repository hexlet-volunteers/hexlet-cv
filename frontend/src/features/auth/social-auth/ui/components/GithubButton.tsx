import { Link } from '@inertiajs/react'
import { Button } from '@mantine/core'
import { GithubIcon } from '@mantinex/dev-icons'

/**
 * Описывает пропсы кнопки авторизации через GitHub.
 */
type GithubButtonProps = {
  /** Текстовое содержимое кнопки. */
  children: React.ReactNode
  /** URL перехода к OAuth-эндпоинту GitHub. */
  href: string
}

/**
 * Отображает кнопку входа через GitHub.
 */
export function GithubButton(props: GithubButtonProps) {
  return (
    <Button
      component={Link}
      href={props.href}
      leftSection={<GithubIcon size={16} color="#00ACEE" />}
      radius="xl"
      variant="default"
    >
      {props.children}
    </Button>
  )
}
