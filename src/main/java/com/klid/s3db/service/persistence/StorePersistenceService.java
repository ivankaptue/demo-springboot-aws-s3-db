package com.klid.s3db.service.persistence;

import com.klid.s3db.service.persistence.entity.StoreEntity;
import com.klid.s3db.service.persistence.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Ivan Kaptue
 */
@SuppressWarnings("unused")
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
    return storeRepository.saveAndFlush(storeEntity);
  }

  @Retryable(
    noRetryFor = {DataIntegrityViolationException.class},
    maxAttemptsExpression = "#{${app.retry.maxAttempts}}",
    backoff = @Backoff(delayExpression = "#{${app.retry.backoffDelay}}"))
  public Page<StoreEntity> findAll(int page, int size) {
    log.info("Get stores. Page : {}, Size: {}", page, size);
    var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
    return storeRepository.findAll(pageable);
  }

  @Retryable(
    noRetryFor = {DataIntegrityViolationException.class},
    maxAttemptsExpression = "#{${app.retry.maxAttempts}}",
    backoff = @Backoff(delayExpression = "#{${app.retry.backoffDelay}}"))
  public Optional<StoreEntity> findById(UUID id) {
    log.info("Get store by id {}", id);
    return storeRepository.findById(id.toString());
  }

  @Recover
  public StoreEntity saveFallback(Exception ex, StoreEntity storeEntity) {
    log.error("Call to DB ended with error. Entity {}", storeEntity, ex);
    throw new DatabaseException("An error occur when saving StoreEntity", ex, storeEntity);
  }

  @Recover
  public Page<StoreEntity> findAllFallback(Exception ex, int page, int size) {
    log.error("Call to DB ended with error. Page {}, Size {}", page, size, ex);
    throw new DatabaseException("An error occur when find all stores", ex, null);
  }

  @Recover
  public Optional<StoreEntity> findByIdFallback(Exception ex, UUID id) {
    log.error("Call to DB ended with error. ID {}", id, ex);
    throw new DatabaseException("An error occur when find store by id", ex, id);
  }
}
