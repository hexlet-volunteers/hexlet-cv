import { Container, Title, Text, Card, Group, Stack } from '@mantine/core';
import { useTranslation } from 'react-i18next';

export const TotaAi: React.FC = () => {
    const { t } = useTranslation();

    return (
        <Container size="lg" py="xs">
            <Card radius="lg">
                <Group justify="center">
                    <Stack gap="xs" align="center">
                        <Text size="md">
                            {t('homePage.totaAi.aiAssistant')}
                        </Text>
                        <Title order={2} ta="center">
                            {t('homePage.totaAi.title')}
                        </Title>
                        <Text ta="center" size="md">
                            {t('homePage.totaAi.description')}
                        </Text>
                    </Stack>
                </Group>
            </Card>
        </Container>
    );
};
