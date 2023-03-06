package com.klid.s3db.acceptance.steps;

import com.klid.s3db.acceptance.context.TestContext;
import com.klid.s3db.model.enums.StatusEnum;
import com.klid.s3db.service.persistence.repository.StoreRepository;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RequiredArgsConstructor
public class StoreSteps {

  private final TestContext testContext;
  private final StoreRepository storeRepository;
  private final MockMvc mockMvc;

  @Then("Store current file name is saved in datastore with status {string}")
  public void storeCurrentFileNameIsSavedInDatastoreWithStatus(String status) {
    var storeOptional = storeRepository.findAll()
      .stream()
      .findFirst();
    assertThat(storeOptional).isNotEmpty();

    var store = storeOptional.get();
    testContext.setSavedStore(store);

    assertThat(store).isNotNull();
    assertThat(store.getId()).isNotBlank();
    assertThat(store.getName()).isEqualTo(testContext.getFilename());
    assertThat(store.getStatus()).isEqualTo(StatusEnum.valueOf(status));
  }

  @When("Get stores")
  public void getStores() throws Exception {
    var resultActions = mockMvc.perform(get("/stores").queryParam("page", "1").queryParam("size", "10"))
      .andDo(print())
      .andExpect(status().isOk());
    testContext.setResultActions(resultActions);
  }

  @Then("Saved stores are returned")
  public void savedStoresAreReturned() throws Exception {
    testContext.getResultActions()
      .andExpect(jsonPath("$.totalPages").value(1))
      .andExpect(jsonPath("$.numberOfElements").value(1))
      .andExpect(jsonPath("$.totalElements").value(1))
      .andExpect(jsonPath("$..content.length()").value(1))
      .andExpect(jsonPath("$..content[0].name").value(testContext.getFilename()));
  }
}
