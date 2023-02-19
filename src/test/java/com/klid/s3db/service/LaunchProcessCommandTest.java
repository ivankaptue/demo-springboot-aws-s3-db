package com.klid.s3db.service;

import com.klid.s3db.exception.S3DBException;
import com.klid.s3db.model.enums.StatusEnum;
import com.klid.s3db.service.converter.SaleItemConverter;
import com.klid.s3db.service.persistence.SalePersistenceService;
import com.klid.s3db.service.persistence.StorePersistenceService;
import com.klid.s3db.service.persistence.entity.SaleEntity;
import com.klid.s3db.service.persistence.entity.StoreEntity;
import com.klid.s3db.service.storage.StorageService;
import com.klid.s3db.service.validator.LineEntryValidator;
import com.klid.s3db.utils.ReaderProvider;
import com.klid.s3db.utils.UUIDGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/**
 * @author Ivan Kaptue
 */
@ExtendWith(MockitoExtension.class)
class LaunchProcessCommandTest {

  private static final String FILE_NAME = "file.csv";
  private static final String UUID = "2359b8e8-27de-4072-bbd8-63ff73b0341a";

  @Mock
  private StorageService storageService;
  @Mock
  private LineEntryValidator lineEntryValidator;
  @Mock
  private SaleItemConverter saleItemConverter;
  @Mock
  private StorePersistenceService storePersistenceService;
  @Mock
  private SalePersistenceService salePersistenceService;
  @Mock
  private UUIDGenerator uuidGenerator;
  @Mock
  private ReaderProvider readerProvider;
  @InjectMocks
  private LaunchProcessCommand launchProcessCommand;

  @Test
  void shouldSalesFromFileToDB() {
    // given
    given(storageService.getFileAsInputStream(FILE_NAME)).willReturn(getInputStream());
    given(uuidGenerator.uuid()).willReturn(UUID);
    given(readerProvider.provideReader(any())).willCallRealMethod();
    willCallRealMethod().given(lineEntryValidator).validate(anyString());
    given(saleItemConverter.convert(any(String.class))).willCallRealMethod();
    given(storePersistenceService.save(any(StoreEntity.class))).will(extractPassedArgument());
    given(salePersistenceService.save(any(SaleEntity.class))).will(extractPassedArgument());

    // when
    var processedItemsCount = launchProcessCommand.execute(FILE_NAME);

    // then
    var storeCaptor = ArgumentCaptor.forClass(StoreEntity.class);
    var saleCaptor = ArgumentCaptor.forClass(SaleEntity.class);
    var expectedStoreEntity = buildStoreEntity();

    assertThat(processedItemsCount).isEqualTo(5);
    then(storageService).should().getFileAsInputStream(FILE_NAME);
    then(storePersistenceService).should().save(storeCaptor.capture());
    assertThat(storeCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedStoreEntity);
    then(lineEntryValidator).should(times(5)).validate(anyString());
    then(saleItemConverter).should(times(5)).convert(anyString());
    then(salePersistenceService).should(times(5)).save(saleCaptor.capture());
    assertThat(saleCaptor.getAllValues())
      .allMatch(sale ->
        UUID.equals(sale.getId()) && Objects.equals(sale.getStoreEntity(), expectedStoreEntity));
  }

  @Test
  void shouldThrowIOExceptionWhenInputStreamCreateException() {
    given(readerProvider.provideReader(any())).willAnswer(invocation -> {
      throw new IOException();
    });

    assertThatThrownBy(() -> launchProcessCommand.execute(FILE_NAME))
      .isInstanceOf(S3DBException.class)
      .hasMessage(String.format("An error occur on processing file %s", FILE_NAME));

    then(storePersistenceService).shouldHaveNoInteractions();
    then(salePersistenceService).shouldHaveNoInteractions();
    then(uuidGenerator).shouldHaveNoInteractions();
    then(lineEntryValidator).shouldHaveNoInteractions();
    then(saleItemConverter).shouldHaveNoInteractions();
  }

  private StoreEntity buildStoreEntity() {
    return StoreEntity.builder()
      .id(UUID)
      .name(FILE_NAME)
      .status(StatusEnum.PENDING)
      .build();
  }

  private InputStream getInputStream() {
    return getClass().getResourceAsStream("/data/valid_input.csv");
  }

  private static <T> Answer<T> extractPassedArgument() {
    return invocation -> invocation.getArgument(0);
  }
}
