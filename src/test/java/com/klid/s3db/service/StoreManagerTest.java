package com.klid.s3db.service;

import com.klid.s3db.data.StoreBuilder;
import com.klid.s3db.exception.ArgumentValidationException;
import com.klid.s3db.service.mapper.StoreMapper;
import com.klid.s3db.service.persistence.StorePersistenceService;
import com.klid.s3db.service.validator.ArgumentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StoreManagerTest {

  @Spy
  private ArgumentValidator argumentValidator;
  @Spy
  private StoreMapper storeMapper;
  @Mock
  private StorePersistenceService storePersistenceService;
  @InjectMocks
  private StoreManager storeManager;

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -2, -10})
  void shouldThrowArgumentValidationExceptionWhenPageIsLessThanOne(int page) {
    assertThatThrownBy(() -> storeManager.getAll(page, 20))
      .isInstanceOf(ArgumentValidationException.class)
      .hasMessage("Page must be greater than 1");
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 5, 9, -1})
  void shouldThrowArgumentValidationExceptionWhenSizeIsLessThanTen(int size) {
    assertThatThrownBy(() -> storeManager.getAll(1, size))
      .isInstanceOf(ArgumentValidationException.class)
      .hasMessage("Size must be greater than 10");
  }

  @Test
  void shouldReturnPageOfStoreFoundInDatabase() {
    int page = 1;
    int size = 10;
    var stores = StoreBuilder.createTenStoreEntities();
    var pageResult = new PageImpl<>(stores, PageRequest.of(0, size), stores.size());
    given(storePersistenceService.findAll(0, size)).willReturn(pageResult);

    var actualPage = storeManager.getAll(page, size);

    then(argumentValidator).should().validate(page, size);
    then(storeMapper).should().mapFromStoreEntities(stores);
    then(storePersistenceService).should().findAll(0, size);
    assertThat(actualPage).isNotNull();
    assertThat(actualPage.getContent()).hasSize(10);
    assertThat(actualPage.getTotalElements()).isEqualTo(10);
    assertThat(actualPage.getPageable().getOffset()).isZero();
    assertThat(actualPage.getPageable().getPageSize()).isEqualTo(10);
  }
}
