package com.klid.s3db.service.persistence;

import com.klid.s3db.service.persistence.entity.SaleEntity;
import com.klid.s3db.service.persistence.entity.StoreEntity;
import com.klid.s3db.service.persistence.repository.SaleRepository;
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

/**
 * @author Ivan Kaptue
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class SalePersistenceService {

  private final SaleRepository saleRepository;

  @Retryable(
    noRetryFor = {DataIntegrityViolationException.class},
    exceptionExpression = DBRetryService.EXPRESSION,
    maxAttemptsExpression = "#{${app.retry.maxAttempts}}",
    backoff = @Backoff(delayExpression = "#{${app.retry.backoffDelay}}"))
  public SaleEntity save(SaleEntity saleEntity) {
    log.info("Save sale entity : {}", saleEntity);
    return saleRepository.save(saleEntity);
  }

  @Retryable(
    noRetryFor = {DataIntegrityViolationException.class},
    maxAttemptsExpression = "#{${app.retry.maxAttempts}}",
    backoff = @Backoff(delayExpression = "#{${app.retry.backoffDelay}}"))
  public Page<SaleEntity> findAll(int page, int size, StoreEntity storeEntity) {
    log.info("Get sales. Page : {}, Size: {}, Store: {}", page, size, storeEntity.getId());
    var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "product"));
    return saleRepository.findAllByStoreEntity(storeEntity, pageable);
  }

  @SuppressWarnings("unused")
  @Recover
  public SaleEntity saveFallback(Exception ex, SaleEntity saleEntity) {
    log.error("Call to DB ended with error. Entity {}", saleEntity, ex);
    throw new DatabaseException("An error occur when saving SaleEntity", ex, saleEntity);
  }

  @SuppressWarnings("unused")
  @Recover
  public Page<SaleEntity> findAllFallback(Exception ex, int page, int size, StoreEntity storeEntity) {
    log.error("Call to DB ended with error. Page {}, Size {}, Store {}", page, size, storeEntity.getId(), ex);
    throw new DatabaseException("An error occur when find all sales", ex, null);
  }
}
