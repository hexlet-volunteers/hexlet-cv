package io.hexlet.cv.handler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClientException extends RuntimeException {

    private final String field;
    private final HttpStatus status;

    public ClientException(String field, String message, HttpStatus status) {
        super(message);
        this.field = field;
        this.status = status;
    }

    public ClientException(String message) {
        this("error", message, HttpStatus.BAD_REQUEST);
    }

}
