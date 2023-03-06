package com.klid.s3db.acceptance.config;

import com.klid.s3db.acceptance.context.TestContext;
import com.klid.s3db.acceptance.drivers.FileReaderDriver;
import com.klid.s3db.model.Constants;
import io.cucumber.spring.ScenarioScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.s3.S3Client;

import static org.mockito.Mockito.mock;

@Lazy
@TestConfiguration
@Profile(Constants.PROFILE_INTEGRATION_TEST)
public class AcceptanceTestConfig {

  @Bean
  S3Client s3Client() {
    return mock(S3Client.class);
  }

  @Bean
  FileReaderDriver fileReaderDriver() {
    return new FileReaderDriver();
  }

  @ScenarioScope
  @Bean
  TestContext testContext() {
    return new TestContext();
  }
}
