package com.klid.s3db.service.exception;

import com.klid.s3db.exception.*;
import com.klid.s3db.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.*;

class ServiceExceptionHandlerTest {

  private final ServiceExceptionHandler serviceExceptionHandler = new ServiceExceptionHandler();

  @ParameterizedTest
  @MethodSource("storageServiceExceptions")
  void shouldReturnRightStatusForStorageServiceException(StorageServiceException ex) {
    var response = serviceExceptionHandler.handleStorageServiceException(ex);

    assertThat(response.getStatusCode()).isEqualTo(ex.getHttpStatus());
    assertErrorResponse(response.getBody(), ex.getHttpStatus().value(), ex.getMessage());
  }

  @Test
  void shouldReturnServiceUnavailableWhenDatabaseExceptionOccurWithDataIntegrityViolationException() {
    var ex = new DatabaseException("An error occur when saving StoreEntity", new DataIntegrityViolationException("Duplicate entry"), null);

    var response = serviceExceptionHandler.handleDatabaseException(ex);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertErrorResponse(response.getBody(), BAD_REQUEST.value(), "Duplicate entry");
  }

  @Test
  void shouldReturnServiceUnavailableWhenDatabaseExceptionOccurWithoutDataIntegrityViolationException() {
    var ex = new DatabaseException("An error occur when saving StoreEntity", new TransactionTimedOutException("Transaction timed out"), null);

    var response = serviceExceptionHandler.handleDatabaseException(ex);

    assertThat(response.getStatusCode()).isEqualTo(SERVICE_UNAVAILABLE);
    assertErrorResponse(response.getBody(), SERVICE_UNAVAILABLE.value(), "Service unavailable");
  }

  @ParameterizedTest
  @MethodSource("badRequestExceptions")
  void shouldReturnBadRequestWhenBadRequestException(Exception ex) {
    var response = serviceExceptionHandler.handleBadRequestExceptions(ex);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertErrorResponse(response.getBody(), BAD_REQUEST.value(), ex.getMessage());
  }

  @Test
  void shouldReturnMethodNotAllowedWhenMethodNotSupportedException() {
    var ex = new HttpRequestMethodNotSupportedException("GET", Collections.singleton("POST"));

    var response = serviceExceptionHandler.handleHttpRequestMethodNotSupportedException(ex);

    assertThat(response.getStatusCode()).isEqualTo(METHOD_NOT_ALLOWED);
    assertErrorResponse(response.getBody(), METHOD_NOT_ALLOWED.value(), "Method 'GET' is not supported.");
  }

  @Test
  void shouldReturnNotFoundWhenStoreEntityNotFoundException() {
    var ex = new StoreEntityNotFoundException(UUID.fromString("6106af5a-70fe-4274-ae7a-c675c68ee074"));

    var response = serviceExceptionHandler.handleStoreEntityNotFound(ex);

    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    assertErrorResponse(response.getBody(), NOT_FOUND.value(), "Store entity not found, id : 6106af5a-70fe-4274-ae7a-c675c68ee074");
  }

  @Test
  void shouldReturnInternalServerErrorWhenUnexpectedException() {
    var ex = new Exception();

    var response = serviceExceptionHandler.handleException(ex);

    assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    assertErrorResponse(response.getBody(), INTERNAL_SERVER_ERROR.value(), "Unexpected internal error");

  }

  private static void assertErrorResponse(@Nullable ErrorResponse response, int errorCode, String detail) {
    assertThat(response).isNotNull();
    assertThat(response.errorCode()).isEqualTo(errorCode);
    assertThat(response.errorDetail()).isEqualTo(detail);
  }

  public static Stream<Arguments> storageServiceExceptions() {
    return Stream.of(
      arguments(new StorageServiceException("An error occur on processing file file.txt", new IOException(), HttpStatus.INTERNAL_SERVER_ERROR)),
      arguments(new StorageServiceException("Key file.txt does not exist", NoSuchKeyException.create("not found", null), HttpStatus.NOT_FOUND)),
      arguments(new StorageServiceException("Client Error when calling S3 Service", SdkClientException.create("error"), HttpStatus.INTERNAL_SERVER_ERROR))
    );
  }

  public static Stream<Arguments> badRequestExceptions() {
    return Stream.of(
      arguments(new ValidationException("line 10 is not valid")),
      arguments(new ArgumentValidationException("page must be a positive number")),
      arguments(new SaleConversionException("Cannot convert line 10", new NumberFormatException())),
      arguments(mock(MethodArgumentTypeMismatchException.class)),
      arguments(new ServletRequestBindingException("Header If-Match not found")),
      arguments(new MissingServletRequestParameterException("filename", "String"))
    );
  }
}
