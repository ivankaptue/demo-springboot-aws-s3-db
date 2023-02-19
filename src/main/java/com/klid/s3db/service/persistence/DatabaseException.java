package com.klid.s3db.service.persistence;

import java.io.Serializable;

/**
 * @author Ivan Kaptue
 */
public class DatabaseException extends RuntimeException {

  private final Serializable entity;

  public DatabaseException(String message, Throwable cause, Serializable entity) {
    super(message, cause);
    this.entity = entity;
  }

  public Serializable getEntity() {
    return entity;
  }
}
