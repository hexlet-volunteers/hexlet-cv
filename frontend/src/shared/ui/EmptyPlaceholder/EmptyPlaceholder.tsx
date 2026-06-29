import { Stack, Title, Button, Text } from '@mantine/core'

interface EmptyPlaceholderProps {
  title: string
  buttonLabel?: string
  buttonLink?: string
  icon?: React.ElementType
}

export const EmptyPlaceholder: React.FC<EmptyPlaceholderProps> = (props) => {
  const { buttonLink, buttonLabel, title, icon: Icon } = props
  return (
    <Stack align="center">
      <Title order={2} ta="center">
        {title}
      </Title>

      {Icon && (
        <Text c="dimmed">
          <Icon size={128} />{' '}
        </Text>
      )}

      {buttonLink && buttonLabel && (
        <Button variant="light" component="a" href={buttonLink}>
          {buttonLabel}
        </Button>
      )}
    </Stack>
  )
}
