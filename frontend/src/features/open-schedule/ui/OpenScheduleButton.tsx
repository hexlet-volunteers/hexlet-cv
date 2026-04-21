import { Button } from '@mantine/core'
import { IconPlus } from '@tabler/icons-react'
import { Link } from '@inertiajs/react'

interface IProps {
  children: React.ReactNode
  variant?: string
}

/**
 * Кнопка перехода к общему расписанию вебинаров.
 * Рендерит ссылку на `/sсhedule` через Mantine Button.
 */
export const OpenScheduleButton: React.FC<IProps> = ({
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
