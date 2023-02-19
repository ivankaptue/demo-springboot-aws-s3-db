package com.klid.s3db.utils;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Ivan Kaptue
 */
class ReaderProviderTest {

  private final ReaderProvider readerProvider = new ReaderProvider();

  @Test
  void shouldReturnBufferedReaderInstanceFromInputStream() {
    var reader = readerProvider.provideReader(new ByteArrayInputStream("Hello world.".getBytes(StandardCharsets.UTF_8)));

    assertThat(reader).isNotNull();
    assertThat(reader.lines().collect(Collectors.joining())).isEqualTo("Hello world.");
  }
}
