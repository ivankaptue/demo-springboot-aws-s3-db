package com.klid.s3db.service;

import com.klid.s3db.data.SaleBuilder;
import com.klid.s3db.exception.ArgumentValidationException;
import com.klid.s3db.exception.StoreEntityNotFound;
import com.klid.s3db.service.mapper.SaleMapper;
import com.klid.s3db.service.persistence.SalePersistenceService;
import com.klid.s3db.service.persistence.StorePersistenceService;
import com.klid.s3db.service.persistence.entity.StoreEntity;
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

import java.util.Optional;
import java.util.UUID;

import static com.klid.s3db.data.StoreBuilder.createStoreEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SaleManagerTest {

    private static final String id = UUID.randomUUID().toString();

    @Spy
    private ArgumentValidator argumentValidator;
    @Spy
    private SaleMapper saleMapper;
    @Mock
    private SalePersistenceService salePersistenceService;
    @Mock
    private StorePersistenceService storePersistenceService;
    @InjectMocks
    private SaleManager saleManager;

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -2, -10})
    void shouldThrowArgumentValidationExceptionWhenPageIsLessThanOne(int page) {
        assertThatThrownBy(() -> saleManager.getAll(page, 20, id))
            .isInstanceOf(ArgumentValidationException.class)
            .hasMessage("Page must be greater than 1");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5, 9, -1})
    void shouldThrowArgumentValidationExceptionWhenSizeIsLessThanTen(int size) {
        assertThatThrownBy(() -> saleManager.getAll(1, size, id))
            .isInstanceOf(ArgumentValidationException.class)
            .hasMessage("Size must be greater than 10");
    }

    @Test
    void shouldThrowArgumentValidationExceptionWhenStoreIdIsNull() {
        assertThatThrownBy(() -> saleManager.getAll(1, 10, null))
            .isInstanceOf(ArgumentValidationException.class)
            .hasMessage("Id must be defined");
    }

    @Test
    void shouldThrowArgumentValidationExceptionWhenStoreIdIsNotAValidUUID() {
        assertThatThrownBy(() -> saleManager.getAll(1, 10, "null"))
            .isInstanceOf(ArgumentValidationException.class)
            .hasMessage("Id must be of type UUID");
    }

    @Test
    void shouldThrowStoreEntityNotFoundWhenStoreIdNotSaved() {
        given(storePersistenceService.findById(any(UUID.class))).willReturn(Optional.empty());

        assertThatThrownBy(() -> saleManager.getAll(1, 10, id))
            .isInstanceOf(StoreEntityNotFound.class)
            .hasMessage("Store entity not found")
            .extracting("id")
            .isEqualTo(UUID.fromString(id));
    }

    @Test
    void shouldReturnPageOfSaleFoundInDatabase() {
        int page = 1;
        int size = 10;
        var storeEntity = createStoreEntity();
        var sales = SaleBuilder.createSaleEntities(10);
        var pageResult = new PageImpl<>(sales, PageRequest.of(0, size), sales.size());
        given(storePersistenceService.findById(any(UUID.class))).willReturn(Optional.of(storeEntity));
        given(salePersistenceService.findAll(anyInt(), anyInt(), any(StoreEntity.class))).willReturn(pageResult);

        var actualPage = saleManager.getAll(page, size, id);

        then(argumentValidator).should().validate(page, size);
        then(saleMapper).should().mapFromSaleEntities(sales);
        then(storePersistenceService).should().findById(UUID.fromString(id));
        then(salePersistenceService).should().findAll(0, size, storeEntity);
        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent()).hasSize(10);
        assertThat(actualPage.getTotalElements()).isEqualTo(10);
        assertThat(actualPage.getPageable().getOffset()).isZero();
        assertThat(actualPage.getPageable().getPageSize()).isEqualTo(10);
    }
}
