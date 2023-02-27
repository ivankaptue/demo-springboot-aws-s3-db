package com.klid.s3db.acceptance.steps;

import com.klid.s3db.acceptance.context.TestContext;
import com.klid.s3db.acceptance.drivers.FileReaderDriver;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RequiredArgsConstructor
public class StorageSteps {

  private final FileReaderDriver fileReaderDriver;
  private final TestContext testContext;
  private final S3Client s3Client;

  @Given("Sales data of today available in remote storage with name {string}")
  public void salesDataOfTodayAvailableInRemoteStorageWithName(String filename) {
    testContext.setFilename(filename);

    given(s3Client.getObject(any(GetObjectRequest.class))).willAnswer(invocation -> {
      GetObjectRequest request = invocation.getArgument(0);
      assertThat(request.bucket()).isEqualTo("bucket-name");
      assertThat(request.key()).isEqualTo(filename);
      var inputStream = fileReaderDriver.readFile(filename);
      return new ResponseInputStream<>(mock(GetObjectResponse.class), AbortableInputStream.create(inputStream));
    });
  }
}
