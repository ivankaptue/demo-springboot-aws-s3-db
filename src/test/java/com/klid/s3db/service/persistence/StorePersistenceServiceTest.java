package com.klid.s3db.service.persistence;

import com.klid.s3db.service.persistence.entity.StoreEntity;
import com.klid.s3db.service.persistence.repository.StoreRepository;
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

import java.util.Optional;
import java.util.UUID;

import static com.klid.s3db.data.StoreBuilder.createStoreEntity;
import static com.klid.s3db.data.StoreBuilder.createTenStoreEntities;
import static org.assertj.core.api.Assertions.assertThat;
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

  @BeforeEach
  void setup() {
    reset(storeRepository);
  }

  @Test
  void shouldSaveStoreEntity() {
    var storeEntity = createStoreEntity();
    given(storeRepository.saveAndFlush(any(StoreEntity.class))).willReturn(storeEntity);

    var savedEntity = storePersistenceService.save(storeEntity);

    then(storeRepository).should().saveAndFlush(storeEntity);
    assertThat(savedEntity).isNotNull();
    assertThat(savedEntity.getName()).isEqualTo("MAXI");
  }

  @Test
  void shouldRetryToSaveEntityWhenExceptionOccur() {
    var storeEntity = createStoreEntity();
    var exception = new RuntimeException("Timeout");
    given(storeRepository.saveAndFlush(storeEntity)).willThrow(exception);

    assertThatThrownBy(() -> storePersistenceService.save(storeEntity))
      .isInstanceOf(DatabaseException.class)
      .hasMessage("An error occur when saving StoreEntity")
      .hasCause(exception);

    then(storeRepository).should(times(2)).saveAndFlush(storeEntity);
    then(dbRetryService).should(times(2)).shouldRetry(exception);
  }

  @Test
  void shouldNotRetryToSaveEntityWhenDataIntegrityViolationOccur() {
    var storeEntity = createStoreEntity();
    given(storeRepository.saveAndFlush(storeEntity)).willThrow(new DuplicateKeyException("Duplicate key exception"));

    assertThatThrownBy(() -> storePersistenceService.save(storeEntity))
      .isInstanceOf(DatabaseException.class)
      .hasMessage("An error occur when saving StoreEntity")
      .hasCauseInstanceOf(DuplicateKeyException.class)
      .hasRootCauseMessage("Duplicate key exception");

    then(storeRepository).should().saveAndFlush(storeEntity);
  }

  @Test
  void shouldReturnsStoresPagingWhenFindAll() {
    var stores = createTenStoreEntities();
    var pagedStore = new PageImpl<>(stores, PageRequest.of(0, 10), stores.size());
    given(storeRepository.findAll(any(Pageable.class))).willReturn(pagedStore);

    var expectedResult = storePersistenceService.findAll(0, 10);

    then(storeRepository).should().findAll(any(Pageable.class));
    assertThat(expectedResult.getContent()).hasSize(10);
  }

  @Test
  void shouldRetryToFindAllStoresWhenExceptionOccur() {
    var exception = new RuntimeException("Timeout");
    given(storeRepository.findAll(any(Pageable.class))).willThrow(exception);

    assertThatThrownBy(() -> storePersistenceService.findAll(0, 10))
      .isInstanceOf(DatabaseException.class)
      .hasMessage("An error occur when find all stores")
      .hasCause(exception);

    then(storeRepository).should(times(2)).findAll(any(Pageable.class));
  }

  @Test
  void shouldNotRetryToFindAllWhenDataIntegrityViolationOccur() {
    var exception = new DataIntegrityViolationException("DataIntegrityViolationException");
    given(storeRepository.findAll(any(Pageable.class))).willThrow(exception);

    assertThatThrownBy(() -> storePersistenceService.findAll(0, 10))
      .isInstanceOf(DatabaseException.class)
      .hasMessage("An error occur when find all stores")
      .hasCauseInstanceOf(DataIntegrityViolationException.class)
      .hasRootCauseMessage("DataIntegrityViolationException");

    then(storeRepository).should().findAll(any(Pageable.class));
  }

  @Test
  void shouldReturnStoreWhenFindById() {
    var id = UUID.randomUUID();
    var storeEntity = createStoreEntity();
    given(storeRepository.findById(anyString())).willReturn(Optional.of(storeEntity));

    var expectedResult = storePersistenceService.findById(id);

    then(storeRepository).should().findById(id.toString());
    assertThat(expectedResult).isPresent();
  }

  @Test
  void shouldRetryToFindStoreByIdWhenExceptionOccur() {
    var id = UUID.randomUUID();
    var exception = new RuntimeException("Timeout");
    given(storeRepository.findById(anyString())).willThrow(exception);

    assertThatThrownBy(() -> storePersistenceService.findById(id))
      .isInstanceOf(DatabaseException.class)
      .hasMessage("An error occur when find store by id")
      .hasCause(exception);

    then(storeRepository).should(times(2)).findById(id.toString());
  }

  @Test
  void shouldNotRetryToFindStoreByIdWhenDataIntegrityViolationOccur() {
    var id = UUID.randomUUID();
    var exception = new DataIntegrityViolationException("DataIntegrityViolationException");
    given(storeRepository.findById(anyString())).willThrow(exception);

    assertThatThrownBy(() -> storePersistenceService.findById(id))
      .isInstanceOf(DatabaseException.class)
      .hasMessage("An error occur when find store by id")
      .hasCauseInstanceOf(DataIntegrityViolationException.class)
      .hasRootCauseMessage("DataIntegrityViolationException");

    then(storeRepository).should().findById(id.toString());
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
