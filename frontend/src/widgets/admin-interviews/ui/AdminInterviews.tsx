import { Anchor, Title, Table, Checkbox, Button, TextInput, Group, Container } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { useState } from 'react'
import { Link } from '@inertiajs/react'

export type InterviewsEntry = {
    id: number
    title: string
    speaker: string
    videoUrl: string
    isPublished: boolean
}

export type TProps = {
    interviews: InterviewsEntry[]
}

export const AdminInterviews: React.FC<TProps> = (props): JSX.Element => {
    const { interviews } = props
    const { t } = useTranslation()

    const [search, setSearch] = useState('')

    const filteredInterviews = interviews?.filter(interview => 
        interview.title.toLowerCase().includes(search.toLowerCase()) || interview.speaker.toLowerCase().includes(search.toLowerCase())
    )

    return (
        <Container fluid py='md'>
            <Title order={2} mb='md' fw={500}>
                {t('adminPage.interviews.title')}
            </Title>
            <Group gap='xs' mb='md'>
                <TextInput
                    placeholder={t('adminPage.interviews.input')}
                    value={search}
                    w={240}
                    onChange={e => setSearch(e.currentTarget.value)}
                />
                <Button component={Link} href='' variant='default'>{t('adminPage.interviews.button')}</Button>
            </Group>
            <Table withTableBorder verticalSpacing='md'>
                <Table.Thead>
                    <Table.Tr>
                        <Table.Th fz='md' py='xs' w='27%'>{t('adminPage.interviews.interviewTitle')}</Table.Th>
                        <Table.Th fz='md' py='xs' w='27%'>{t('adminPage.interviews.interviewSpeaker')}</Table.Th>
                        <Table.Th fz='md' py='xs' w='31%'>{t('adminPage.interviews.interviewVideo')}</Table.Th>
                        <Table.Th fz='md' py='xs' w='15%' ta='center'>{t('adminPage.interviews.interviewPublished')}</Table.Th>
                    </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                    {filteredInterviews?.map((interview) => (
                        <Table.Tr key={interview.id}>
                            <Table.Td>
                                <Anchor underline='not-hover'>
                                    {interview.title}
                                </Anchor>
                            </Table.Td>
                            <Table.Td>
                                <TextInput value={interview.speaker} readOnly size='xs' w={200} style={{ pointerEvents: 'none' }}/>
                            </Table.Td>
                            <Table.Td>
                                <TextInput value={interview.videoUrl} readOnly size='xs' w={200} style={{ pointerEvents: 'none' }}/>
                            </Table.Td>
                            <Table.Td ta='center'>
                                { /* Пока не знаю, как должны функционировать чекбоксы и какую информацию они должны отправлять */ }
                                <Checkbox checked={interview.isPublished} style={{ display: 'inline-block' }} size='xs' readOnly/>
                            </Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
        </Container>
    )
}