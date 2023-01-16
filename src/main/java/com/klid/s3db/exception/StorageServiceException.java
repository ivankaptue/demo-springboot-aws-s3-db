package com.klid.s3db.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Ivan Kaptue
 */
public class StorageServiceException extends RuntimeException {

    private final HttpStatus httpStatus;

    public StorageServiceException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
