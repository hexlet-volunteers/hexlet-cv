import {
  Card,
  Title,
  Text,
  Container,
  Button,
  Stack,
  Image,
  useMantineTheme,
  Paper,
} from '@mantine/core'
import { Carousel } from '@mantine/carousel'
import { useTranslation } from 'react-i18next'
import type { SuccessCardDto } from '../types'
import { Link } from '@inertiajs/react'
import { useState } from 'react'

/**
 * Массив данных о успешных историях.
 * Если данные отсутствуют или массив пустой, секция не отображается.
 */
type TProps = {
  successStories?: SuccessCardDto[] | null
}

/**
 * Виджет "Истории успеха" для главной страницы.
 * Отображает карусель карточек историй.
 *
 * @remarks
 * Виджет скрывается полностью, если:
 * - Данные не переданы (undefined/null)
 * - Передан пустой массив
 *
 * Карточка истории содержит:
 * - Изображение (или плейсхолдер, если изображение отсутствует или не загрузилось)
 * - Заголовок
 * - Описание
 * - Кнопка перехода к полной истории
 *
 * @param props - данные историй 
 * @returns React-компонент или null, если нет данных
 */
export const SuccessStories: React.FC<TProps> = (props) => {
  const [imageError, setImageError] = useState(false)
  const theme = useMantineTheme()
  const { successStories } = props
  const { t } = useTranslation()

  if (!successStories || successStories.length === 0) {
    return null
  }

  /**
   * Отрисовывает карточку каждой истории.
   *
   * @param item - Данные истории {@link SuccessCardDto}
   * @returns JSX элемент карточки с изображением, заголовком и описанием
   *
   * @remarks
   * В качестве key используется уникальный идентификатор `item.id`.
   * 
   * Обработка изображения:
   * - Если `imageUrl` отсутствует или произошла ошибка загрузки,
   *   отображается плейсхолдер в виде темного блока (`Paper`)
   * - При успешной загрузке отображается изображение
   * 
   * Кнопка ведет на полную версию истории (если передан `item.link`).
   */
  const renderItem = (item: SuccessCardDto) => {
    const { description, id, imageUrl, link, title } = item
    return (
      <Carousel.Slide key={id}>
        <Card p="sm" radius="lg" h="100%">
          <Stack gap={0} h="100%">
            {(!imageUrl || imageError) ? (
              <Paper h={180} bg={theme.colors.dark[8]} radius="md" />
            ) : (
              <Image
                src={imageUrl}
                height={180}
                radius="lg"
                alt={title}
                fit="fill"
                loading="lazy"
                onError={() => setImageError(true)}
              />
            )}
            <Text size="sm" fw="bold" mt={10}>
              {title}
            </Text>
            <Text size="xs" flex={1}>
              {description}
            </Text>
            <Button
              component={Link}
              href={link || '#'}
              mt="md"
              w="fit-content"
              radius="lg"
            >
              {t('homePage.successStories.button')}
            </Button>
          </Stack>
        </Card>
      </Carousel.Slide>
    )
  }

  return (
    <Container size="lg" py="xl">
      <Title order={1} mb="md">
        {t('homePage.successStories.title')}
      </Title>

      <Carousel
        height="100%"
        type="container"
        slideSize={{
          base: '100%',
          '480px': '50%',
          '730px': '33.333%',
          '900px': '25%',
        }}
        slideGap={{
          base: 'xs',
          '480px': 'sm',
          '730px': 'md',
          '900px': 'lg',
        }}
        emblaOptions={{
          loop: true,
          align: 'start',
        }}
      >
        {successStories.map(renderItem)}
      </Carousel>
    </Container>
  )
}