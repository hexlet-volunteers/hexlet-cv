import { Button, Text } from '@mantine/core'

type TProps = {
  label: string
  onClick?: () => void
}

export const DsCardAction: React.FC<TProps> = (props) => {
  const { label, onClick } = props

  return (

    <Button
      fullWidth
      mt="md"
      ml="0"
      size="md"
      variant="white"
      color="var(--mantine-color-dark-7)"
      radius="lg"
      onClick={onClick}
    >
      <Text fw={400}>{label}</Text>
    </Button>

  )
}
