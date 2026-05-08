/**
 * Описывает один пункт административного меню.
 */
type AdminMenuItemDTO = {
  /** Отображаемое название пункта меню. */
  label: string
  /** URL перехода в административный раздел. */
  link: string
  /** Ключ иконки из карты `iconsMap`. */
  icon: string
}

/**
 * Описывает группу пунктов административного меню.
 */
export type AdminMenuDTO = {
  /** Название группы пунктов меню. */
  category: string
  /** Список пунктов внутри группы. */
  items: AdminMenuItemDTO[]
}
