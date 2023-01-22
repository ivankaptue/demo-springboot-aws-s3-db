package com.klid.s3db.service.persistence;

import com.klid.s3db.service.persistence.entity.StoreEntity;
import com.klid.s3db.service.persistence.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * @author Ivan Kaptue
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class StorePersistenceService {

    private final StoreRepository storeRepository;

    @Retryable(
        noRetryFor = {DataIntegrityViolationException.class},
        exceptionExpression = DBRetryService.EXPRESSION,
        maxAttemptsExpression = "#{${app.retry.maxAttempts}}",
        backoff = @Backoff(delayExpression = "#{${app.retry.backoffDelay}}"))
    public StoreEntity save(StoreEntity storeEntity) {
        log.info("Save store entity {}", storeEntity);
        return storeRepository.save(storeEntity);
    }

    @SuppressWarnings("unused")
    @Recover
    public StoreEntity saveFallback(Exception ex, StoreEntity storeEntity) {
        log.error("Call to DB ended with error. Entity {}", storeEntity, ex);
        throw new DatabaseException("An error occur when saving StoreEntity", ex, storeEntity);
    }
}
