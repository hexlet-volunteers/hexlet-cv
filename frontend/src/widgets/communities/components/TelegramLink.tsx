import { Text } from '@mantine/core'
import { openTelegramLink } from '../helpers/openTelegramInNewTab'
import { DsCard } from '@shared/uikit/DsCard/DsCard'

const FONT_SIZE_TELEGRAM_TITLE = 'lg'
const FONT_SIZE_TELEGRAM_TEXT = 'xs'
const STYLE_CURSOR_POINTER = { cursor: 'pointer' }

type TProps = {
  text: string
  title: string
  username: string
}

export const TelegramLink: React.FC<TProps> = (props) => {
  const { text, title, username } = props

  return (
    <DsCard
      aria-label={`Open ${title} on Telegram`}
      onClick={() => {
        openTelegramLink(username)
      }}
      style={STYLE_CURSOR_POINTER}
    >
      <DsCard.Content>
        <Text c="white" fw={500} fz={FONT_SIZE_TELEGRAM_TITLE} ta="left">
          {title}
        </Text>
        <Text c="white" fz={FONT_SIZE_TELEGRAM_TEXT} ta="left">
          {text}
        </Text>
      </DsCard.Content>
    </DsCard>
  )
}
