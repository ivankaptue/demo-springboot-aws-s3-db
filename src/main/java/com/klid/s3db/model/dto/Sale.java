package com.klid.s3db.model.dto;

import java.math.BigDecimal;

public record Sale(
  String id,
  String product,
  int quantity,
  BigDecimal price
) {
}
