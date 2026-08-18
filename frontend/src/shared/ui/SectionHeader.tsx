import { Stack, Text, Title } from '@mantine/core'

/**
 * Свойства компонента {@link SectionHeader}.
 */
interface SectionHeaderProps {
  /** Заголовок секции, отображаемый крупным шрифтом */
  title: string
  /** Подзаголовок или описание секции, отображаемое под заголовком */
  description: string
}

/**
 * Компонент для отображения заголовка и описания секции.
 *
 * Автоматически адаптирует ширину под мобильные и десктопные экраны.
 * Использует библиотеку Mantine UI для стилизации и разметки.
 *
 * @param props - Входные параметры компонента типа {@link SectionHeaderProps}
 * @returns Элемент структуры разметки заголовка секции
 */
export function SectionHeader({ title, description }: SectionHeaderProps) {
  return (
    <Stack gap={0} mb={36} ta="start" w={{ base: '100%', sm: '50%' }}>
      <Title order={2} fz={30} mb={10}>
        {title}
      </Title>
      <Text c="rgb(74, 79, 89)" fz={14.5}>
        {description}
      </Text>
    </Stack>
  )
}
