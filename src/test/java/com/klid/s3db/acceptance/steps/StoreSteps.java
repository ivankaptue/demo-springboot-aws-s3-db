package com.klid.s3db.acceptance.steps;

import com.klid.s3db.acceptance.context.TestContext;
import com.klid.s3db.model.enums.StatusEnum;
import com.klid.s3db.service.persistence.repository.StoreRepository;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;


@RequiredArgsConstructor
public class StoreSteps {

  private final TestContext testContext;
  private final StoreRepository storeRepository;

  @Then("Store current file name is saved in datastore with status {string}")
  public void storeCurrentFileNameIsSavedInDatastoreWithStatus(String status) {
    var storeOptional = storeRepository.findAll().stream().findFirst();
    assertThat(storeOptional).isNotEmpty();

    var store = storeOptional.get();
    testContext.setSavedStore(store);

    assertThat(store).isNotNull();
    assertThat(store.getId()).isNotBlank();
    assertThat(store.getName()).isEqualTo(testContext.getFilename());
    assertThat(store.getStatus()).isEqualTo(StatusEnum.valueOf(status));
  }
}
