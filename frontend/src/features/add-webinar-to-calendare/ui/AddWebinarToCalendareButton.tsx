import { Button } from '@mantine/core'
import { Link } from '@inertiajs/react'

interface IProps {
  itemId: number
  children: React.ReactNode
  variant?: string
}

/**
 * Кнопка добавления вебинара в календарь.
 * Рендерит Inertia Link через Mantine Button по URL с `itemId`.
 */
export const AddWebinarToCalendareButton: React.FC<IProps> = ({
  itemId,
  children,
  variant = 'subtle',
}) => {
  // TODO: заменить на реальную ссылку
  const href = `/calendare/${itemId}/register`
  return (
    <Button href={href} component={Link} variant={variant}>
      {children}
    </Button>
  )
}
