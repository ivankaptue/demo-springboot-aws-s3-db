package com.klid.s3db.service;

import com.klid.s3db.exception.StorageServiceException;
import com.klid.s3db.model.enums.StatusEnum;
import com.klid.s3db.service.converter.SaleItemConverter;
import com.klid.s3db.service.persistence.SalePersistenceService;
import com.klid.s3db.service.persistence.StorePersistenceService;
import com.klid.s3db.service.persistence.entity.StoreEntity;
import com.klid.s3db.service.storage.StorageService;
import com.klid.s3db.service.validator.LineEntryValidator;
import com.klid.s3db.utils.ReaderProvider;
import com.klid.s3db.utils.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Ivan Kaptue
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class LaunchProcessCommand {

  private final StorageService storageService;
  private final LineEntryValidator lineEntryValidator;
  private final SaleItemConverter saleItemConverter;
  private final StorePersistenceService storePersistenceService;
  private final SalePersistenceService salePersistenceService;
  private final UUIDGenerator uuidGenerator;
  private final ReaderProvider readerProvider;

  public long execute(String filename) {
    log.info("Start processing file {}", filename);

    var contentInputStream = storageService.getFileAsInputStream(filename);
    try (var bufferedReader = readerProvider.provideReader(contentInputStream)) {
      var storeEntity = createStoreEntity(filename);
      var count = processData(storeEntity, bufferedReader);

      log.info("End processing file {}. {} items processed", filename, count);

      return count;
    } catch (IOException ex) {
      var message = String.format("An error occur on processing file %s", filename);
      throw new StorageServiceException(message, ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private StoreEntity createStoreEntity(String filename) {
    var storeEntity = StoreEntity.builder()
      .id(uuidGenerator.uuid())
      .name(filename)
      .status(StatusEnum.PENDING)
      .build();

    return storePersistenceService.save(storeEntity);
  }

  private long processData(StoreEntity storeEntity, BufferedReader bufferedReader) {
    var currentLine = new AtomicLong(0);

    return bufferedReader.lines()
      .skip(1)
      .peek(item -> currentLine.incrementAndGet())
      .filter(StringUtils::hasText)
      .peek(lineEntryValidator::validate)
      .map(saleItemConverter::convert)
      .peek(item -> item.setId(uuidGenerator.uuid()))
      .peek(item -> item.setStoreEntity(storeEntity))
      .map(salePersistenceService::save)
      .peek(item -> log.info("Current line : {}, item saved Id: {}", currentLine.get(), item.getId()))
      .count();
  }
}
