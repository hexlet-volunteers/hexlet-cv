import { Group, ActionIcon } from '@mantine/core'
import { socialIcons } from '../model/footerConfig'
import { type SocialIcon } from '../model/types'

function SocialLink({ iconConfig }: { iconConfig: SocialIcon }) {
  const IconComponent = iconConfig.icon

  return (
    <ActionIcon
      component="a"
      href={iconConfig.link}
      target="_blank"
      rel="noopener noreferrer"
      variant="filled"
      color="gray.9"
      size="lg"
      radius="md"
      aria-label={`Мы в ${iconConfig.label}`}
    >
      <IconComponent size={15} color="#B9BECC" stroke={1.2} />
    </ActionIcon>
  )
}

export function FooterSocialLinks() {
  return (
    <Group gap="xs">
      {socialIcons.map((icon) => (
        <SocialLink key={icon.id} iconConfig={icon} />
      ))}
    </Group>
  )
}
