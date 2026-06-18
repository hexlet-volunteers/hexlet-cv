/**
 * Объект, представляющий один избранный материал (курс или статью).
 */

export type FavoriteDTO = {
  id: number
  type: 'course' | 'article'
  title: string
  url: string
}
