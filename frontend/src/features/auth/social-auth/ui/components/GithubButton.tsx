import { Link } from '@inertiajs/react'
import { Button } from '@mantine/core'
import { GithubIcon } from '@mantinex/dev-icons'

export function GithubButton(props: { children: React.ReactNode, href: string }) {
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
