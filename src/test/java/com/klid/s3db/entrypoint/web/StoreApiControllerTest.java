package com.klid.s3db.entrypoint.web;

import com.klid.s3db.data.StoreBuilder;
import com.klid.s3db.service.SaleManager;
import com.klid.s3db.service.StoreManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import static com.klid.s3db.data.SaleBuilder.createSales;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreApiController.class)
class StoreApiControllerTest {

    @MockBean
    private StoreManager storeManager;

    @MockBean
    private SaleManager saleManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnPageOfStoreWhenFound() throws Exception {
        var stores = StoreBuilder.createStores(10);
        var page = new PageImpl<>(stores, PageRequest.of(0, 10), 17);
        given(storeManager.getAll(1, 10)).willReturn(page);

        mockMvc.perform(get("/stores").queryParam("page", "1").queryParam("size", "10"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.numberOfElements").value(10))
            .andExpect(jsonPath("$.totalElements").value(17))
            .andExpect(jsonPath("$..content.length()").value(10))
            .andExpect(jsonPath("$..content[0].name").value("Maxi 1"));
    }

    @Test
    void shouldReturnPageOfSaleWhenFound() throws Exception {
        var id = "a84d54d9-62aa-44a3-9b89-3c50e322ee29";
        var sales = createSales(10);
        var page = new PageImpl<>(sales, PageRequest.of(0, 10), 17);
        given(saleManager.getAll(1, 10, id)).willReturn(page);

        mockMvc.perform(
                get("/stores/a84d54d9-62aa-44a3-9b89-3c50e322ee29/sales")
                    .queryParam("page", "1")
                    .queryParam("size", "10")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.numberOfElements").value(10))
            .andExpect(jsonPath("$.totalElements").value(17))
            .andExpect(jsonPath("$..content.length()").value(10))
            .andExpect(jsonPath("$..content[0].product").value("Meal 1"));
    }
}
