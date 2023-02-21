package com.klid.s3db.service.exception;

import com.klid.s3db.exception.*;
import com.klid.s3db.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.springframework.http.HttpStatus.*;

/**
 * @author Ivan Kaptue
 */
@Slf4j
@ControllerAdvice
public class ServiceExceptionHandler {

  @ExceptionHandler(StorageServiceException.class)
  public ResponseEntity<ErrorResponse> handleStorageServiceException(StorageServiceException ex) {
    log.error("Storage service Error", ex);
    return ResponseEntity.status(ex.getHttpStatus()).body(buildErrorResponse(ex.getHttpStatus().value(), ex.getMessage()));
  }

  @ExceptionHandler(DatabaseException.class)
  public ResponseEntity<ErrorResponse> handleDatabaseException(DatabaseException ex) {
    log.error("Database communication Error", ex);
    if (ex.getCause() instanceof DataIntegrityViolationException dive) {
      return ResponseEntity.status(BAD_REQUEST).body(buildErrorResponse(BAD_REQUEST.value(), dive.getMessage()));
    }
    return ResponseEntity.status(SERVICE_UNAVAILABLE).body(buildErrorResponse(SERVICE_UNAVAILABLE.value(), "Service unavailable"));
  }

  @ExceptionHandler({
    ValidationException.class,
    ArgumentValidationException.class,
    SaleConversionException.class,
    MethodArgumentTypeMismatchException.class,
    ServletRequestBindingException.class,
    MissingServletRequestParameterException.class})
  public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception ex) {
    log.warn("Bad request", ex);
    return ResponseEntity.status(BAD_REQUEST).body(buildErrorResponse(BAD_REQUEST.value(), ex.getMessage()));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
    log.error("Method not supported", ex);
    return ResponseEntity.status(METHOD_NOT_ALLOWED).body(buildErrorResponse(METHOD_NOT_ALLOWED.value(), ex.getBody().getDetail()));
  }

  @ExceptionHandler(StoreEntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleStoreEntityNotFound(StoreEntityNotFoundException ex) {
    log.warn("Store not found", ex);
    return ResponseEntity.status(NOT_FOUND).body(buildErrorResponse(NOT_FOUND.value(), String.format("%s, id : %s", ex.getMessage(), ex.getId())));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    log.error("Unexpected Error", ex);
    return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(buildErrorResponse(INTERNAL_SERVER_ERROR.value(), "Unexpected internal error"));
  }

  private static ErrorResponse buildErrorResponse(int code, String detail) {
    return new ErrorResponse(code, detail);
  }
}
