package io.hexlet.cv.service.mail;

import io.hexlet.cv.model.User;

/**
 * Отправка письма со ссылкой на сброс пароля.
 */
public interface PasswordResetMailer {

    /**
     * Отправляет письмо со ссылкой.
     *
     * @param user получатель
     * @param link ссылка на страницу нового пароля, уже с токеном
     */
    void send(User user, String link);
}
