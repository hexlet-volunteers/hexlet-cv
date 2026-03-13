import { Button } from '@mantine/core'
import { IconPlus } from '@tabler/icons-react'
import { Link } from '@inertiajs/react'

interface IProps {
  children: React.ReactNode
  variant?: string
}
export const OpenSheduleButton: React.FC<IProps> = ({
  children,
  variant = 'subtle',
}) => {
  return (
    <Button
      href={`/shedule`}
      component={Link}
      variant={variant}
      leftSection={<IconPlus size={14} />}
    >
      {children}
    </Button>
  )
}
