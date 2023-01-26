package com.klid.s3db.service;

import com.klid.s3db.exception.S3DBException;
import com.klid.s3db.model.enums.StatusEnum;
import com.klid.s3db.service.converter.SaleItemConverter;
import com.klid.s3db.service.persistence.SalePersistenceService;
import com.klid.s3db.service.persistence.StorePersistenceService;
import com.klid.s3db.service.persistence.entity.StoreEntity;
import com.klid.s3db.service.storage.StorageService;
import com.klid.s3db.utils.ReaderProvider;
import com.klid.s3db.utils.UUIDProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Ivan Kaptue
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class LaunchProcessCommand {

    private final StorageService storageService;
    private final SaleItemConverter saleItemConverter;
    private final StorePersistenceService storePersistenceService;
    private final SalePersistenceService salePersistenceService;
    private final UUIDProvider uuidProvider;
    private final ReaderProvider readerProvider;

    public void execute(String filename) {
        log.info("Start processing file {}", filename);

        var contentInputStream = storageService.getFileAsInputStream(filename);
        try (var bufferedReader = readerProvider.provideReader(contentInputStream)) {
            var storeEntity = createStoreEntity(filename);
            var count = processData(storeEntity, bufferedReader);

            log.info("End processing file {}. {} items processed", filename, count);
        } catch (IOException ex) {
            var message = String.format("An error occur on processing file %s", filename);
            throw new S3DBException(message, ex);
        }
    }

    private StoreEntity createStoreEntity(String filename) {
        var storeEntity = StoreEntity.builder()
                .id(uuidProvider.uuid())
                .name(filename)
                .status(StatusEnum.PENDING)
                .build();

        return storePersistenceService.save(storeEntity);
    }

    private long processData(StoreEntity storeEntity, BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .skip(1)
                .filter(StringUtils::hasText)
                .map(saleItemConverter::convert)
                .peek(item -> item.setId(uuidProvider.uuid()))
                .peek(item -> item.setStoreEntity(storeEntity))
                .map(salePersistenceService::save)
                .count();
    }
}
