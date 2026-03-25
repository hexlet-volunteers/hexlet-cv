import { Container, Card, Badge, Text, Group, Title, Grid, List, ThemeIcon } from '@mantine/core';
import { useTranslation } from 'react-i18next';

export const CommercialProjects: React.FC = () => {
    const { t } = useTranslation();

    return (
        <Container size="lg" py="xs">
            <Card radius="lg" p="xl" bg="dark.6">
                <Group justify="center" mb="xl">
                    <Badge color="gray" size="lg" tt="none">
                        <Text size="xs">
                            {t('homePage.commercialProjects.badge')}
                        </Text>
                    </Badge>
                    <Title fz={32} fw="bold" ta="center">
                        {t('homePage.commercialProjects.title')}
                    </Title>
                    <Text size="md" maw={600} ta="center">
                        {t('homePage.commercialProjects.description')}
                    </Text>
                </Group>
                <Grid>
                    <Grid.Col
                        span={{
                            base: 12,
                            md: 6,
                        }}
                    >
                        <Card radius="lg" bg="dark.5" h="100%">
                            <Group gap="sm">
                                <ThemeIcon variant="transparent" color="blue">
                                    <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        viewBox="0 0 22 22"
                                        strokeWidth={2}
                                        stroke="currentColor"
                                        fill="none"
                                    >
                                        <path d="M20.25 14.15v4.25c0 1.094-.787 2.036-1.872 2.18-2.087.277-4.216.42-6.378.42s-4.291-.143-6.378-.42c-1.085-.144-1.872-1.086-1.872-2.18v-4.25m16.5 0a2.18 2.18 0 0 0 .75-1.661V8.706c0-1.081-.768-2.015-1.837-2.175a48.114 48.114 0 0 0-3.413-.387m4.5 8.006c-.194.165-.42.295-.673.38A23.978 23.978 0 0 1 12 15.75c-2.648 0-5.195-.429-7.577-1.22a2.016 2.016 0 0 1-.673-.38m0 0A2.18 2.18 0 0 1 3 12.489V8.706c0-1.081.768-2.015 1.837-2.175a48.111 48.111 0 0 1 3.413-.387m7.5 0V5.25A2.25 2.25 0 0 0 13.5 3h-3a2.25 2.25 0 0 0-2.25 2.25v.894m7.5 0a48.667 48.667 0 0 0-7.5 0M12 12.75h.008v.008H12v-.008Z" />
                                    </svg>
                                </ThemeIcon>
                                <Text fz="h3">
                                    {t('homePage.commercialProjects.benefits.title')}
                                </Text>
                            </Group>
                            <Text size="sm" ml={40} mt="xs">
                                {t('homePage.commercialProjects.benefits.description')}
                            </Text>
                        </Card>
                    </Grid.Col>
                    <Grid.Col
                        span={{
                            base: 12,
                            md: 6,
                        }}
                    >
                        <Card radius="lg" bg="dark.5" h="100%">
                            <Text fz="h3" mb="sm">
                                {t('homePage.commercialProjects.projectsList.title')}
                            </Text>

                            <List size="sm" mr="sm">
                                {(
                                    t('homePage.commercialProjects.projectsList.items', {
                                        returnObjects: true,
                                    }) as string[]
                                ).map((item: string, index: number) => (
                                    <List.Item key={index}>{item}</List.Item>
                                ))}
                            </List>
                        </Card>
                    </Grid.Col>
                </Grid>
            </Card>
        </Container>
    );
};
