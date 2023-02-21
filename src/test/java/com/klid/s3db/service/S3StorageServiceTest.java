package com.klid.s3db.service;

import com.klid.s3db.exception.StorageServiceException;
import com.klid.s3db.service.storage.s3.S3StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

/**
 * @author Ivan Kaptue
 */
@ExtendWith(MockitoExtension.class)
class S3StorageServiceTest {

  private static final String BUCKET_NAME = "bucket-name";
  private static final String KEY = "a84d54d9-62aa-44a3-9b89-3c50e322ee29";

  @Mock
  private S3Client s3Client;

  private S3StorageService s3StorageService;

  @BeforeEach
  void beforeEach() {
    s3StorageService = new S3StorageService(s3Client, BUCKET_NAME);
  }

  @Nested
  @DisplayName("Test getFileAsInputStream from S3 bucket")
  class GetFileTest {

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnsFileContentAsInputStreamFromS3BucketWhenGetFileAsInputStream() {
      var fileName = getFileName();
      var objectRequest = getGetObjectRequest(fileName);
      ResponseInputStream<GetObjectResponse> objectResponse = mock(ResponseInputStream.class);
      given(s3Client.getObject(any(GetObjectRequest.class))).willReturn(objectResponse);

      var inputStream = s3StorageService.getFileAsInputStream(fileName);

      then(s3Client).should().getObject(objectRequest);
      assertThat(inputStream).isEqualTo(objectResponse);
    }

    @Test
    void shouldThrowStorageServiceExceptionWhenS3ClientThrowsNoSuchKeyException() {
      var fileName = getFileName();
      given(s3Client.getObject(any(GetObjectRequest.class))).willThrow(NoSuchKeyException.class);

      assertGetFileContentException(
        fileName,
        NoSuchKeyException.class,
        String.format("Key %s does not exist", fileName),
        HttpStatus.NOT_FOUND
      );
    }

    @Test
    void shouldThrowStorageServiceExceptionWhenS3ClientThrowsSdkClientException() {
      var fileName = getFileName();
      given(s3Client.getObject(any(GetObjectRequest.class))).willThrow(SdkClientException.class);

      assertGetFileContentException(
        fileName,
        SdkClientException.class,
        "Client Error when calling S3 Service",
        HttpStatus.BAD_REQUEST
      );
    }

    @Test
    void shouldThrowStorageServiceExceptionWhenS3ClientThrowsS3Exception() {
      var fileName = getFileName();
      given(s3Client.getObject(any(GetObjectRequest.class))).willThrow(S3Exception.class);

      assertGetFileContentException(
        fileName,
        S3Exception.class,
        "Server Error when calling S3 Service",
        HttpStatus.SERVICE_UNAVAILABLE
      );
    }
  }

  private static String getFileName() {
    return KEY + ".txt";
  }

  private void assertGetFileContentException(
    String key,
    Class<? extends Exception> exceptionCauseClass,
    String exceptionMessage,
    HttpStatus exceptionHttpStatus) {

    assertThatThrownBy(() -> s3StorageService.getFileAsInputStream(key))
      .isInstanceOf(StorageServiceException.class)
      .hasMessage(exceptionMessage)
      .hasCauseInstanceOf(exceptionCauseClass)
      .extracting("httpStatus")
      .isEqualTo(exceptionHttpStatus);
  }

  private GetObjectRequest getGetObjectRequest(String key) {
    return GetObjectRequest.builder()
      .bucket(BUCKET_NAME)
      .key(key)
      .build();
  }
}
