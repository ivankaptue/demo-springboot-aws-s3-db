package com.klid.s3db.acceptance.steps;

import com.klid.s3db.acceptance.context.TestContext;
import com.klid.s3db.service.persistence.entity.SaleEntity;
import com.klid.s3db.service.persistence.repository.SaleRepository;
import io.cucumber.java.en.And;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class SaleSteps {

  private final SaleRepository saleRepository;
  private final TestContext testContext;

  @And("{int} Sales of today are all saved in datastore")
  public void salesOfTodayAreAllSavedInDatastore(int saleCount) {
    Page<SaleEntity> salesPage = saleRepository.findAllByStoreEntity(testContext.getSavedStore(), PageRequest.of(0, 50));
    assertThat(salesPage.getContent()).hasSize(saleCount);
    testContext.setSales(salesPage.getContent());
  }

  @And("Products list {string} are saved")
  public void productsListAreSaved(String products) {
    assertThat(
      testContext.getSales()
        .stream()
        .map(SaleEntity::getProduct)
        .toList()
    ).containsAll(List.of(products.split(",")));
  }
}
