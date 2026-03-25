package io.hexlet.cv.handler.exception;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {

    private final String errorCode;

    public ServerException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ServerException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
