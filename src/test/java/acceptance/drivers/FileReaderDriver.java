package acceptance.drivers;

import lombok.SneakyThrows;

import java.io.InputStream;

public class FileReaderDriver {

  private static final String DATA_DIR = "/data/";

  @SneakyThrows
  public InputStream readFile(String filename) {
    return getClass().getResourceAsStream(DATA_DIR + filename);
  }
}
