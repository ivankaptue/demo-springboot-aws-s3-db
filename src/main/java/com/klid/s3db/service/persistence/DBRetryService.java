package com.klid.s3db.service.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Ivan Kaptue
 */
@Slf4j
@Component("DBRetryService")
public class DBRetryService {

  public static final String EXPRESSION = "@DBRetryService.shouldRetry(#root)";

  @SuppressWarnings("unused")
  public boolean shouldRetry(Exception ex) {
    log.debug("should retry", ex);
    return true;
  }
}
