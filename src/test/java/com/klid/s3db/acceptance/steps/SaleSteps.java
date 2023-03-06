package com.klid.s3db.acceptance.steps;

import com.klid.s3db.acceptance.context.TestContext;
import com.klid.s3db.service.persistence.entity.SaleEntity;
import com.klid.s3db.service.persistence.repository.SaleRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class SaleSteps {

  private final SaleRepository saleRepository;
  private final TestContext testContext;
  private final MockMvc mockMvc;

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

  @When("Get sales")
  public void getSales() throws Exception {
    var savedStoreId = testContext.getSavedStore().getId();

    var resultActions = mockMvc.perform(
        get(String.format("/stores/%s/sales", savedStoreId))
          .queryParam("page", "1")
          .queryParam("size", "10")
      )
      .andDo(print())
      .andExpect(status().isOk());
    testContext.setResultActions(resultActions);
  }

  @Then("Saved sales are returned")
  public void savedSalesAreReturned(DataTable dataTable) throws Exception {
    var dataMaps = dataTable.asMaps(String.class, String.class);

    testContext.getResultActions()
      .andExpect(jsonPath("$.totalPages").value(1))
      .andExpect(jsonPath("$.numberOfElements").value(2))
      .andExpect(jsonPath("$.totalElements").value(2))
      .andExpect(jsonPath("$..content.length()").value(2))
      .andExpect(jsonPath("$..content[0].product").value(dataMaps.get(0).get("product")))
      .andExpect(jsonPath("$..content[0].quantity").value(Integer.parseInt(dataMaps.get(0).get("quantity"))))
      .andExpect(jsonPath("$..content[0].price").value(Double.parseDouble(dataMaps.get(0).get("price"))))
      .andExpect(jsonPath("$..content[1].product").value(dataMaps.get(1).get("product")))
      .andExpect(jsonPath("$..content[1].quantity").value(Integer.parseInt(dataMaps.get(1).get("quantity"))))
      .andExpect(jsonPath("$..content[1].price").value(Double.parseDouble(dataMaps.get(1).get("price"))));
  }
}
