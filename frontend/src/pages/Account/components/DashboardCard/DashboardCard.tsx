import { Card, Flex, Stack, Text, ThemeIcon } from '@mantine/core'

type DashboardCardProps = {
  label: string
  value: string | number
  description?: string
  icon: React.ElementType
}
export const DashboardCard = (props: DashboardCardProps) => {
  const { label, value, description, icon: Icon } = props

  return (
    <Card shadow="sm" padding="md" radius="md" bg="dark.5" withBorder>
      <Flex
        direction={{ base: 'row', lg: 'row' }}
        align={{ base: 'flex-start', sm: 'stretch', lg: 'flex-start' }}
        gap="md"
      >
        <ThemeIcon
          size="lg"
          variant="light"
          radius="md"
          mx={{ base: 0, sm: 'auto', lg: 0 }}
        >
          <Icon size="1.5rem" />
        </ThemeIcon>

        <Stack gap={0} style={{ flex: 1 }}>
          <Text size="xs" fw={500} c="dimmed" ta={{ base: 'left', lg: 'left' }}>
            {label}
          </Text>

          <Text size="xl" fw={700} ta={{ base: 'left', lg: 'left' }}>
            {value}
          </Text>

          {description && (
            <Text size="xs" c="dimmed" ta={{ base: 'left', lg: 'left' }}>
              {description}
            </Text>
          )}
        </Stack>
      </Flex>
    </Card>
  )
}
