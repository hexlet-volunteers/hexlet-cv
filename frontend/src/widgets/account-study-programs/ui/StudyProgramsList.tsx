import {
  SimpleGrid,
  Center,
  Loader,
  Paper,
  Text,
  Group,
  Progress,
  Badge,
} from '@mantine/core'
import { EmptyPlaceholder } from '@shared/ui'
import { IconAlertCircle } from '@tabler/icons-react'
import { IconSchoolOff } from '@tabler/icons-react'
import { useTranslation } from 'react-i18next'
import type { StudyProgramDTO } from '../types'

/**
 * Интерфейс пропсов компонента StudyProgramsList.
 *
 * @prop programs - массив программ обучения пользователя.
 */
interface StudyProgramsListProps {
  programs?: StudyProgramDTO[]
}

/**
 * Виджет для отображения программ обучения пользователя.
 * Рендерит сетку карточек с названием программы, прогрессом и навыками.
 * Если данные не пришли или undefined - показывает лоадер, если массив данных пустой - показывает плейсхолдер пустого списка.
 *
 * @param props - список программ обучения { programs: [...] }
 */
export const StudyProgramsList: React.FC<StudyProgramsListProps> = (props) => {
  const { programs } = props
  const { t } = useTranslation()
  const requiredProgramKeys = ['id', 'title', 'progress', 'skills']

  if (!programs) {
    return (
      <Center h={200}>
        <Loader color="blue" size="lg" />
      </Center>
    )
  }

  if (!Array.isArray(programs)) {
    return (
      <EmptyPlaceholder
        title={t('accountPage.studyPrograms.incorrectData')}
        icon={IconAlertCircle}
      />
    )
  }

  if (
    typeof programs[0] !== 'object' ||
    !requiredProgramKeys.every((key) => Object.hasOwn(programs[0], key))
  ) {
    return (
      <EmptyPlaceholder
        title={t('accountPage.studyPrograms.incorrectData')}
        icon={IconAlertCircle}
      />
    )
  }

  if (!programs.length) {
    return (
      <EmptyPlaceholder
        title={t('accountPage.studyPrograms.noPrograms')}
        icon={IconSchoolOff}
      />
    )
  }

  return (
    <SimpleGrid cols={2} spacing="lg">
      {programs.map((program) => (
        <Paper key={program.id} radius="lg" withBorder p="lg">
          <Group justify="space-between">
            <Text size="lg" fw={500}>
              {program.title}
            </Text>
            <Text>{program.progress}%</Text>
          </Group>
          <Progress my={15} value={program.progress} />
          <Group gap="xs">
            {program.skills.map((skill, index) => (
              <Badge key={index} variant="light" radius="sm">
                {skill}
              </Badge>
            ))}
          </Group>
        </Paper>
      ))}
    </SimpleGrid>
  )
}
