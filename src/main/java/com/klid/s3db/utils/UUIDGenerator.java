package com.klid.s3db.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Ivan Kaptue
 */
@Component
public class UUIDGenerator {

  public static final String UUID_PATTERN = "([a-fA-F0-9]{8}(-[a-fA-F0-9]{4}){4}[a-fA-F0-9]{8})";

  public String uuid() {
    return UUID.randomUUID().toString();
  }
}
