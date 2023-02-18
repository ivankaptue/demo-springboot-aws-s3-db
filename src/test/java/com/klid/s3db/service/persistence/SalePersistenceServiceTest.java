package com.klid.s3db.service.persistence;

import com.klid.s3db.service.persistence.entity.SaleEntity;
import com.klid.s3db.service.persistence.entity.StoreEntity;
import com.klid.s3db.service.persistence.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.EnableRetry;

import static com.klid.s3db.data.SaleBuilder.createSaleEntities;
import static com.klid.s3db.data.SaleBuilder.createSaleEntity;
import static com.klid.s3db.data.StoreBuilder.createStoreEntity;
import static org.assertj.core.api.Assertions.assertThat;
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

    @BeforeEach
    void setup() {
        reset(saleRepository);
    }

    @Test
    void shouldSaveSaleEntity() {
        var saleEntity = createSaleEntity();
        given(saleRepository.save(any(SaleEntity.class))).willReturn(saleEntity);

        var savedEntity = salePersistenceService.save(saleEntity);

        then(saleRepository).should().save(saleEntity);
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getProduct()).isEqualTo("Milk");
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

    @Test
    void shouldReturnsSalesPagingWhenFindAll() {
        var storeEntity = createStoreEntity();
        var sales = createSaleEntities(7);
        var pagedSale = new PageImpl<>(sales, PageRequest.of(0, 10), 17);
        given(saleRepository.findAllByStoreEntity(any(StoreEntity.class), any(Pageable.class))).willReturn(pagedSale);

        var expectedResult = salePersistenceService.findAll(0, 10, storeEntity);

        then(saleRepository).should().findAllByStoreEntity(eq(storeEntity), any(Pageable.class));
        assertThat(expectedResult.getContent()).hasSize(7);
        assertThat(expectedResult.getTotalPages()).isEqualTo(2);
        assertThat(expectedResult.getTotalElements()).isEqualTo(17);
    }

    @Test
    void shouldRetryToFindAllSalesWhenExceptionOccur() {
        var storeEntity = createStoreEntity();
        var exception = new RuntimeException("Timeout");
        given(saleRepository.findAllByStoreEntity(any(StoreEntity.class), any(Pageable.class))).willThrow(exception);

        assertThatThrownBy(() -> salePersistenceService.findAll(0, 10, storeEntity))
            .isInstanceOf(DatabaseException.class)
            .hasMessage("An error occur when find all sales")
            .hasCause(exception);

        then(saleRepository).should(times(2)).findAllByStoreEntity(eq(storeEntity), any(Pageable.class));
    }

    @Test
    void shouldNotRetryToFindAllWhenDataIntegrityViolationOccur() {
        var storeEntity = createStoreEntity();
        var exception = new DataIntegrityViolationException("DataIntegrityViolationException");
        given(saleRepository.findAllByStoreEntity(any(StoreEntity.class), any(Pageable.class))).willThrow(exception);

        assertThatThrownBy(() -> salePersistenceService.findAll(0, 10, storeEntity))
            .isInstanceOf(DatabaseException.class)
            .hasMessage("An error occur when find all sales")
            .hasCauseInstanceOf(DataIntegrityViolationException.class)
            .hasRootCauseMessage("DataIntegrityViolationException");

        then(saleRepository).should().findAllByStoreEntity(eq(storeEntity), any(Pageable.class));
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
