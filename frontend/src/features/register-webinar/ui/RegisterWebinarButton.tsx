import { Button } from '@mantine/core'
import { Link } from '@inertiajs/react'

interface IProps {
  webinarId: number
  children: React.ReactNode
  variant?: string
}
export const RegisterWebinarButton: React.FC<IProps> = ({
  webinarId,
  children,
  variant = 'subtle',
}) => {
  // TODO: заменить на реальную ссылку
  const href = `/webinar/${webinarId}/register`
  return (
    <Button href={href} component={Link} variant={variant}>
      {children}
    </Button>
  )
}
