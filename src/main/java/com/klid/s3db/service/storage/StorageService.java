package com.klid.s3db.service.storage;

import java.io.InputStream;

/**
 * @author Ivan Kaptue
 */
public interface StorageService {

  InputStream getFileAsInputStream(String key);
}
