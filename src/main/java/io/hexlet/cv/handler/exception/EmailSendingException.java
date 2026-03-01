package io.hexlet.cv.handler.exception;

import org.springframework.mail.MailException;

public class EmailSendingException extends RuntimeException {

    public EmailSendingException(String message, MailException e) {
        super(message);
    }
}
