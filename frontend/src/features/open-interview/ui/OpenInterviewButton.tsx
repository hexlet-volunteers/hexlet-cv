import { Button } from '@mantine/core'
import { IconChevronRight } from '@tabler/icons-react'
import { Link } from '@inertiajs/react'

interface IProps {
  interviewId: number
  children: React.ReactNode
  variant?: string
}

export const OpenInterviewButton: React.FC<IProps> = ({
  children,
  interviewId,
  variant = 'subtle',
}) => {
  return (
    <Button
      href={`/account/interviews/${interviewId}/read`}
      component={Link}
      variant={variant}
      rightSection={<IconChevronRight size={14} />}
    >
      {children}
    </Button>
  )
}
