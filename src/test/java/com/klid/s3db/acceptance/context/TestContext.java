package com.klid.s3db.acceptance.context;

import com.klid.s3db.service.persistence.entity.SaleEntity;
import com.klid.s3db.service.persistence.entity.StoreEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TestContext {
  private String filename;
  private StoreEntity savedStore;
  private List<SaleEntity> sales;
}
