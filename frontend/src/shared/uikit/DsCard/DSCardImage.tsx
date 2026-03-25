import { Card, Image } from '@mantine/core'

type TProps = {
  src?: string
}

const CARD_IMAGE_HEIGHT = 160

const FALLBACK_STYLE_PROPS: React.CSSProperties = {
  background: 'linear-gradient(135deg, #1b2233 0%, #2a4b5d 55%, #2a3136 100%)',
  height: CARD_IMAGE_HEIGHT,
}

export const DSCardImage: React.FC<TProps> = (props) => {
  const { src } = props

  if (src) {
    return (
      <Card.Section>
        <Image
          src={src}
          h={CARD_IMAGE_HEIGHT}
        />
      </Card.Section>
    )
  }

  return (
    <Card.Section>
      <div style={FALLBACK_STYLE_PROPS} />
    </Card.Section>
  )
}
