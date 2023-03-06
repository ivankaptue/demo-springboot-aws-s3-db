package com.klid.s3db.acceptance.hooks;

import com.klid.s3db.service.persistence.repository.SaleRepository;
import com.klid.s3db.service.persistence.repository.StoreRepository;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AcceptanceHooks {

  private final StoreRepository storeRepository;
  private final SaleRepository saleRepository;

  @Before
  public void setup() {
    resetRepositories();
  }

  private void resetRepositories() {
    saleRepository.deleteAll();
    storeRepository.deleteAll();
    saleRepository.flush();
    storeRepository.flush();
  }
}
