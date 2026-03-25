import { Button } from '@mantine/core'
import { IconChevronRight } from '@tabler/icons-react'
import { Link } from '@inertiajs/react'

interface IProps {
  programId: number
  children: React.ReactNode
  variant?: string,
}
export const OpenProgramButton: React.FC<IProps> = ({
  children,
  programId,
  variant = 'subtle',
}) => {
  return (
    <Button
      href={`/account/my-progress/program/${programId}/lessons`}
      component={Link}
      variant={variant}
      rightSection={<IconChevronRight size={14} />}
    >
      {children}
    </Button>
  )
}
