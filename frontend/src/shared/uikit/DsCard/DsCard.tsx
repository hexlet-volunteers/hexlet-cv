import { Card, type CardProps } from '@mantine/core'
import { DsCardContent } from './DsCardContent'
import { DsCardAction } from './DsCardAction'
import { DSCardImage } from './DSCardImage'

type DsCardProps = CardProps & {
  children: React.ReactNode
  onClick?: () => void
}

type DsCardFC = React.FC<DsCardProps> & {
  Action: typeof DsCardAction
  Content: typeof DsCardContent
  Image: typeof DSCardImage
}

export const DsCard: DsCardFC = (props) => {
  const { children, style, ...rest } = props

  return (
    <Card
      {...rest}
      padding="sm"
      radius="lg"
      style={{
        ...style,
        position: 'relative',
        transition: 'transform 0.2s ease',
      }}
      withBorder
    >
      {children}
    </Card>
  )
}

DsCard.Action = DsCardAction
DsCard.Content = DsCardContent
DsCard.Image = DSCardImage
