package com.klid.s3db.exception;

/**
 * @author Ivan Kaptue
 */
public class SaleConversionException extends RuntimeException {

    public SaleConversionException(String message) {
        super(message);
    }

    public SaleConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
