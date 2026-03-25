import { Container, Stack } from '@mantine/core'
import { ActivityCards } from './ActivityCards'
import classes from './SectionLayout.module.css'

type TProps = {
  children: React.ReactNode
}

export const SectionLayout: React.FC<TProps> = (props) => {
  const { children } = props
  return (
    <Container fluid pt="md" flex={1}>
      <Stack gap="md">
        <ActivityCards />
        <main className={classes.section}>{children}</main>
      </Stack>
    </Container>
  )
}
