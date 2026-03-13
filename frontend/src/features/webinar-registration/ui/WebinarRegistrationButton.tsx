import { Button } from '@mantine/core'
import { Link } from '@inertiajs/react'

interface IProps {
  webinarId: number
  children: React.ReactNode
  variant?: string
}
export const WebinarRegistrationButton: React.FC<IProps> = ({
  webinarId,
  children,
  variant = 'subtle',
}) => {
  return (
    <Button
      // TODO: заменить на реальную ссылку
      href={`/webinar/${webinarId}/register`}
      component={Link}
      variant={variant}
    >
      {children}
    </Button>
  )
}
