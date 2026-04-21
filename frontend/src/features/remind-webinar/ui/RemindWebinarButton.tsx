import { Button } from '@mantine/core'
import { Link } from '@inertiajs/react'
import { IconBellPlus } from '@tabler/icons-react'

interface IProps {
  itemId: number

  variant?: string
}
export const RemindWebinarButton: React.FC<IProps> = ({
  itemId,
  variant = 'subtle',
}) => {
  return (
    <Button
      p={0}
      // TODO: заменить на реальную ссылку
      href={`/remind/${itemId}/register`}
      component={Link}
      variant={variant}
    >
      <IconBellPlus size={24} />
    </Button>
  )
}
