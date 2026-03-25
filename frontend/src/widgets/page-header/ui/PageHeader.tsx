import { Group, ThemeIcon, Title } from '@mantine/core'

type PageHeaderProps = {
  icon: React.ReactNode
  title: string
  actionButton?: React.ReactNode
}

export const PageHeader = (props: PageHeaderProps) => {
  const { icon: Icon, title, actionButton } = props
  return (
    <Group mb="sm" justify="space-between">
      <Group>
        <ThemeIcon size="lg" variant="default" radius="md">
          {Icon}
        </ThemeIcon>
        <Title order={2}>{title}</Title>
      </Group>
      {actionButton}
    </Group>
  )
}
