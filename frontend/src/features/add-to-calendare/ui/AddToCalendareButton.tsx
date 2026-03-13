import { Button } from '@mantine/core'
import { Link } from '@inertiajs/react'

interface IProps {
  itemId: number
  children: React.ReactNode
  variant?: string
}
export const AddToCalendareButton: React.FC<IProps> = ({
  itemId,
  children,
  variant = 'subtle',
}) => {
  return (
    <Button
      // TODO: заменить на реальную ссылку
      href={`/calendar/${itemId}/register`}
      component={Link}
      variant={variant}
    >
      {children}
    </Button>
  )
}
