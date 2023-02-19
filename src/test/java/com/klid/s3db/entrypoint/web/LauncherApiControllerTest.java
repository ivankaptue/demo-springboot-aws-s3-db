package com.klid.s3db.entrypoint.web;

import com.klid.s3db.service.LaunchProcessCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Ivan Kaptue
 */
@WebMvcTest(LauncherApiController.class)
class LauncherApiControllerTest {

  @MockBean
  private LaunchProcessCommand launchProcessCommand;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldReturnsCountOfSalesProcessed() throws Exception {
    given(launchProcessCommand.execute(anyString())).willReturn(5L);

    mockMvc.perform(
        post("/download")
          .queryParam("filename", "MAXI_20230129.csv")
          .accept(MediaType.APPLICATION_JSON_VALUE))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.sales", equalTo(5)));
  }
}
