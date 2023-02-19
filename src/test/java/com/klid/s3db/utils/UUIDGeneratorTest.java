package com.klid.s3db.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Ivan Kaptue
 */
class UUIDGeneratorTest {

  private final UUIDGenerator uuidGenerator = new UUIDGenerator();

  @Test
  void shouldReturnsValidUUID() {
    var uuid = uuidGenerator.uuid();

    assertThat(uuid).isNotBlank();
    assertThat(uuid).matches(UUIDGenerator.UUID_PATTERN);
  }
}
