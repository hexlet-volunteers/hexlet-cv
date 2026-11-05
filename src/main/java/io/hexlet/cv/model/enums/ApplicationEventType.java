package io.hexlet.cv.model.enums;

/**
 * Событие в таймлайне отклика.
 */
public enum ApplicationEventType {
    /** Отклик добавлен в трекер. */
    CREATED,
    /** Смена стадии — пишется автоматически. */
    STAGE_CHANGED,
    /** Ответ компании. */
    REPLY,
    /** Назначенное или прошедшее интервью. */
    INTERVIEW,
    /** Отправленное письмо. */
    LETTER,
    /** Заметка пользователя. */
    NOTE
}
