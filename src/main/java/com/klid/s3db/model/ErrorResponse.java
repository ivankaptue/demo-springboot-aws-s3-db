package com.klid.s3db.model;

import java.io.Serializable;

public record ErrorResponse(
  int errorCode,
  String errorDetail
) implements Serializable {
}
