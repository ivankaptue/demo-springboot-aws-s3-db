package com.klid.s3db.service.storage.s3;

import com.klid.s3db.exception.StorageServiceException;
import com.klid.s3db.service.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;

/**
 * @author Ivan Kaptue
 */
@Service
public class S3StorageService implements StorageService {

  private static final Logger logger = LoggerFactory.getLogger(S3StorageService.class);

  private final S3Client s3Client;
  private final String bucket;

  public S3StorageService(S3Client s3Client, @Value("${app.aws.s3.bucket}") String bucket) {
    this.s3Client = s3Client;
    this.bucket = bucket;
  }

  @Override
  public InputStream getFileAsInputStream(String key) {
    logger.info("Get file content from S3 bucket");

    try {
      var objectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();
      var objectResponse = s3Client.getObject(objectRequest);

      logger.info("File content found and returned from S3 bucket");

      return objectResponse;
    } catch (NoSuchKeyException ex) {
      throw new StorageServiceException("Key does not exist", ex, HttpStatus.NOT_FOUND);
    } catch (SdkClientException ex) {
      throw new StorageServiceException("Client Error when calling S3 Service", ex, HttpStatus.BAD_REQUEST);
    } catch (S3Exception ex) {
      throw new StorageServiceException("Server Error when calling S3 Service", ex, HttpStatus.SERVICE_UNAVAILABLE);
    }
  }
}
