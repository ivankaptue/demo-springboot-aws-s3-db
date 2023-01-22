package com.klid.s3db.service.persistence;

import com.klid.s3db.service.persistence.entity.SaleEntity;
import com.klid.s3db.service.persistence.entity.StoreEntity;
import com.klid.s3db.service.persistence.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.EnableRetry;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

/**
 * @author Ivan Kaptue
 */
@SpringBootTest(classes = SalePersistenceServiceTest.SalePersistenceContextConfig.class)
class SalePersistenceServiceTest {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private DBRetryService dbRetryService;

    @Autowired
    private SalePersistenceService salePersistenceService;

    @Test
    void shouldSaveSaleEntity() {
        var saleEntity = createSaleEntity();

        salePersistenceService.save(saleEntity);

        then(saleRepository).should().save(saleEntity);
    }

    @Test
    void shouldRetryToSaveEntityWhenExceptionOccur() {
        var saleEntity = createSaleEntity();
        var exception = new RuntimeException("Timeout");
        given(saleRepository.save(saleEntity)).willThrow(exception);

        assertThatThrownBy(() -> salePersistenceService.save(saleEntity))
            .isInstanceOf(DatabaseException.class)
            .hasMessage("An error occur when saving SaleEntity")
            .hasCause(exception);

        then(saleRepository).should(times(2)).save(saleEntity);
        then(dbRetryService).should(times(2)).shouldRetry(exception);
    }

    @Test
    void shouldNotRetryToSaveEntityWhenDataIntegrityViolationOccur() {
        var saleEntity = createSaleEntity();
        given(saleRepository.save(saleEntity)).willThrow(new DuplicateKeyException("Duplicate key exception"));

        assertThatThrownBy(() -> salePersistenceService.save(saleEntity))
            .isInstanceOf(DatabaseException.class)
            .hasMessage("An error occur when saving SaleEntity")
            .hasCauseInstanceOf(DuplicateKeyException.class)
            .hasRootCauseMessage("Duplicate key exception");

        then(saleRepository).should().save(saleEntity);
    }

    SaleEntity createSaleEntity() {
        return SaleEntity.builder()
            .id(UUID.randomUUID().toString())
            .storeEntity(new StoreEntity())
            .product("Milk")
            .price(new BigDecimal("10.00"))
            .quantity((short) 3)
            .build();
    }

    @Configuration
    @EnableRetry
    @Import(SalePersistenceService.class)
    static class SalePersistenceContextConfig {

        @Bean("DBRetryService")
        DBRetryService dbRetryService() {
            return spy(DBRetryService.class);
        }

        @Bean
        SaleRepository saleRepository() {
            return mock(SaleRepository.class);
        }
    }
}
