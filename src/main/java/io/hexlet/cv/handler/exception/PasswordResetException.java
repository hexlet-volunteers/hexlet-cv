package io.hexlet.cv.handler.exception;

public class PasswordResetException extends RuntimeException {

    public PasswordResetException (String message) {
        super(message);
    }
}
