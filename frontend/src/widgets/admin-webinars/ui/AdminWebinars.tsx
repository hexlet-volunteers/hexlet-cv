import { Anchor, Title, Table, Text, Checkbox, Button, TextInput, Group, Container, Center, Loader } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { useState } from 'react'
import { Link } from '@inertiajs/react'

export type WebinarDTO = {
    id: number
    name: string
    date: string
    registration: string
    videoUrl: string
    feature: boolean
    isPublished: boolean
}

interface TProps {
    webinars: WebinarDTO[]
}

export const AdminWebinars: React.FC<TProps> = (props): JSX.Element => {
    const { webinars } = props
    const { t } = useTranslation()

    const [search, setSearch] = useState('')

    if (!webinars) {
        return (
            <Container fluid py='md'>
                <Center h={200}>
                    <Loader color="blue" size="lg" />
                </Center>
            </Container>
        )
    }

    const filteredWebinars = webinars?.filter(webinar => 
        webinar.name.toLowerCase().includes(search.toLowerCase()) || webinar.date.includes(search.toLowerCase()) ||
        webinar.registration.toLowerCase().includes(search.toLowerCase()) || webinar.videoUrl.toLowerCase().includes(search.toLowerCase())
    )

    const renderTable = () => {
        if (webinars.length === 0) {
            return (
                <Text ta='center' py='xl'>
                    {t('adminPage.webinars.noWebinars')}
                </Text>
            )
        }
        else if (filteredWebinars.length === 0) {
            return (
                <Text ta="center" py="xl">
                    {t('adminPage.webinars.nothingFound')}
                </Text>
            )
        } else {
            return (
                <Table withTableBorder verticalSpacing='md'>
                    <Table.Thead>
                        <Table.Tr>
                            <Table.Th fz='md' py='xs' w='15%'>{t('adminPage.webinars.webinarName')}</Table.Th>
                            <Table.Th fz='md' py='xs' w='15%' ta='center'>{t('adminPage.webinars.webinarDate')}</Table.Th>
                            <Table.Th fz='md' py='xs' w='25%' ta='center'>{t('adminPage.webinars.webinarRegistration')}</Table.Th>
                            <Table.Th fz='md' py='xs' w='25%' ta='center'>{t('adminPage.webinars.webinarVideo')}</Table.Th>
                            <Table.Th fz='md' py='xs' w='10%' ta='center'>{t('adminPage.webinars.webinarFeature')}</Table.Th>
                            <Table.Th fz='md' py='xs' w='10%' ta='center'>{t('adminPage.webinars.webinarPublished')}</Table.Th>
                        </Table.Tr>
                    </Table.Thead>
                    <Table.Tbody>
                        {filteredWebinars?.map((webinar) => (
                            <Table.Tr key={webinar.id}>
                                <Table.Td>
                                    <Anchor underline='not-hover'>
                                        {webinar.name}
                                    </Anchor>
                                </Table.Td>
                                <Table.Td ta='center'>
                                    <Text size='md'>{webinar.date}</Text>
                                </Table.Td>
                                <Table.Td>
                                    <Center>
                                        <TextInput value={webinar.registration} readOnly size='xs' w={225} style={{ pointerEvents: 'none' }}/>
                                    </Center>
                                </Table.Td>
                                <Table.Td>
                                    <Center>
                                        <TextInput value={webinar.videoUrl} readOnly size='xs' w={225} style={{ pointerEvents: 'none' }}/>
                                    </Center>
                                </Table.Td>
                                <Table.Td ta='center'>
                                    { /* Пока не знаю, как должны функционировать чекбоксы и какую информацию они должны отправлять */ }
                                    <Checkbox checked={webinar.feature} style={{ display: 'inline-block' }} size='xs' readOnly/>
                                </Table.Td>
                                <Table.Td ta='center'>
                                    { /* Пока не знаю, как должны функционировать чекбоксы и какую информацию они должны отправлять */ }
                                    <Checkbox checked={webinar.isPublished} style={{ display: 'inline-block' }} size='xs' readOnly/>
                                </Table.Td>
                            </Table.Tr>
                        ))}
                    </Table.Tbody>
                </Table>
            )
        }
    }

    return (
        <Container fluid py='md'>
            <Title order={2} mb='md' fw={500}>
                {t('adminPage.webinars.title')}
            </Title>
            <Group gap='xs' mb='md'>
                <TextInput
                    placeholder={t('adminPage.webinars.input')}
                    value={search}
                    w={240}
                    onChange={e => setSearch(e.currentTarget.value)}
                />
                <Button component={Link} href='' variant='default'>{t('adminPage.webinars.button')}</Button>
            </Group>
            {renderTable()}
        </Container>
    )
}
