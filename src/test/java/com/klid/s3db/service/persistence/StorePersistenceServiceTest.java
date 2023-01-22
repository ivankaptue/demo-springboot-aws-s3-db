package com.klid.s3db.service.persistence;

import com.klid.s3db.model.enums.StatusEnum;
import com.klid.s3db.service.persistence.entity.StoreEntity;
import com.klid.s3db.service.persistence.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.EnableRetry;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

/**
 * @author Ivan Kaptue
 */
@SpringBootTest(classes = StorePersistenceServiceTest.StorePersistenceTestConfig.class)
class StorePersistenceServiceTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private DBRetryService dbRetryService;

    @Autowired
    private StorePersistenceService storePersistenceService;

    @Test
    void shouldSaveStoreEntity() {
        var storeEntity = createStoreEntity();

        storePersistenceService.save(storeEntity);

        then(storeRepository).should().save(storeEntity);
    }

    @Test
    void shouldRetryToSaveEntityWhenExceptionOccur() {
        var storeEntity = createStoreEntity();
        var exception = new RuntimeException("Timeout");
        given(storeRepository.save(storeEntity)).willThrow(exception);

        assertThatThrownBy(() -> storePersistenceService.save(storeEntity))
            .isInstanceOf(DatabaseException.class)
            .hasMessage("An error occur when saving StoreEntity")
            .hasCause(exception);

        then(storeRepository).should(times(2)).save(storeEntity);
        then(dbRetryService).should(times(2)).shouldRetry(exception);
    }

    @Test
    void shouldNotRetryToSaveEntityWhenDataIntegrityViolationOccur() {
        var storeEntity = createStoreEntity();
        given(storeRepository.save(storeEntity)).willThrow(new DuplicateKeyException("Duplicate key exception"));

        assertThatThrownBy(() -> storePersistenceService.save(storeEntity))
            .isInstanceOf(DatabaseException.class)
            .hasMessage("An error occur when saving StoreEntity")
            .hasCauseInstanceOf(DuplicateKeyException.class)
            .hasRootCauseMessage("Duplicate key exception");

        then(storeRepository).should().save(storeEntity);
    }

    StoreEntity createStoreEntity() {
        return StoreEntity.builder()
            .id(UUID.randomUUID().toString())
            .name("MAXI")
            .status(StatusEnum.PENDING)
            .build();
    }

    @Configuration
    @EnableRetry
    @Import(StorePersistenceService.class)
    static class StorePersistenceTestConfig {

        @Bean("DBRetryService")
        DBRetryService dbRetryService() {
            return spy(DBRetryService.class);
        }

        @Bean
        StoreRepository storeRepository() {
            return mock(StoreRepository.class);
        }
    }
}
