import { Button } from '@mantine/core'
import { IconPlus } from '@tabler/icons-react'
import { Link } from '@inertiajs/react'

interface IProps {
  children: React.ReactNode
  variant?: string
}

/**
 * Кнопка открытия формы отзыва.
 * Рендерит ссылку на `/sсhedule` через Mantine Button (Inertia Link).
 */
export const OpenReviewForm: React.FC<IProps> = ({
  children,
  variant = 'subtle',
}) => {
  return (
    <Button
      href={`/sсhedule`}
      component={Link}
      variant={variant}
      leftSection={<IconPlus size={14} />}
    >
      {children}
    </Button>
  )
}
