package com.klid.s3db.exception;

/**
 * @author Ivan Kaptue
 */
public class S3DBException extends RuntimeException {

    public S3DBException(String message, Throwable cause) {
        super(message, cause);
    }
}
