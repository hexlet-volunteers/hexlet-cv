import { Title, Table, Checkbox, Button, TextInput, Group, Container, Anchor, Center, Loader, Text } from '@mantine/core'
import { useTranslation } from 'react-i18next'
import { useState } from 'react'
import { Link } from '@inertiajs/react'

export type KnowledgeBaseEntry = {
    id: number
    title: string
    category: string
    isPublished: boolean
}

export type TProps = {
    articles: KnowledgeBaseEntry[]
}

export const KnowledgeBase: React.FC<TProps> = (props): JSX.Element => {
    const { articles } = props
    const { t } = useTranslation()

    const [search, setSearch] = useState('')

    if (!articles) {
        return (
            <Container fluid py='md'>
                <Center h={200}>
                    <Loader color="blue" size="lg" />
                    {/* Вместо Loader можно будет добавить Skeleton */}
                </Center>
            </Container>
        )
    }

    const filteredArticles = articles?.filter(article => 
        article.title.toLowerCase().includes(search.toLowerCase()) || article.category.toLowerCase().includes(search.toLowerCase())
    )

    const renderTable = () => {
        if (articles.length === 0) {
            return (
                <Text ta='center' py='xl'>
                    {t('adminPage.knowledgeBase.baseIsEmpty')}
                </Text>
            )
        }
        else if (filteredArticles.length === 0) {
            return (
                <Text ta="center" py="xl">
                    {t('adminPage.knowledgeBase.nothingFound')}
                </Text>
            )
        } else {
            return (
                <Table withTableBorder verticalSpacing='md'>
                    <Table.Thead>
                        <Table.Tr>
                            <Table.Th fz='md' py='xs' w='35%'>{t('adminPage.knowledgeBase.articleTitle')}</Table.Th>
                            <Table.Th fz='md' py='xs' w='40%'>{t('adminPage.knowledgeBase.articlecategory')}</Table.Th>
                            <Table.Th fz='md' py='xs' w='25%' ta='center'>{t('adminPage.knowledgeBase.articlePublished')}</Table.Th>
                        </Table.Tr>
                    </Table.Thead>
                    <Table.Tbody>
                        {filteredArticles?.map((article) => (
                            <Table.Tr key={article.id}>
                                <Table.Td>
                                    <Anchor href='' underline='not-hover'>
                                        {article.title}
                                    </Anchor>
                                </Table.Td>
                                <Table.Td>
                                    <TextInput value={article.category} readOnly size='xs' w={200} style={{ pointerEvents: 'none' }}/>
                                </Table.Td>
                                <Table.Td ta='center'>
                                    { /* Пока не знаю, как должны функционировать чекбоксы и какую информацию они должны отправлять */ }
                                    <Checkbox checked={article.isPublished} style={{ display: 'inline-block' }} size='xs' readOnly/>
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
                {t('adminPage.knowledgeBase.title')}
            </Title>
            <Group gap='xs' mb='md'>
                <TextInput
                    placeholder={t('adminPage.knowledgeBase.input')}
                    value={search}
                    w={240}
                    onChange={e => setSearch(e.currentTarget.value)}
                />
                <Button type='submit' component={Link} href='' variant='default'>{t('adminPage.knowledgeBase.button')}</Button>
            </Group>
            {renderTable()}
        </Container>
    ) 
}