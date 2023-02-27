package com.klid.s3db.acceptance.steps;

import com.klid.s3db.acceptance.context.TestContext;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class LoadingProcessSteps {

  private final MockMvc mockMvc;
  private final TestContext testContext;

  @When("The loading process start")
  public void theLoadingProcessStart() throws Exception {
    mockMvc.perform(post("/download").queryParam("filename", testContext.getFilename()))
      .andDo(print())
      .andExpect(status().isOk());
  }
}
