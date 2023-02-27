package com.klid.s3db.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

@Slf4j
public class BaseIntegrationTestConfig {

  static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine3.17");

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    log.info("Start Testcontainers");
    Startables.deepStart(postgresContainer).join();
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
    registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
  }
}
