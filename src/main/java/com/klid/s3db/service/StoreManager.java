package com.klid.s3db.service;

import com.klid.s3db.model.dto.Store;
import com.klid.s3db.service.mapper.StoreMapper;
import com.klid.s3db.service.persistence.StorePersistenceService;
import com.klid.s3db.service.validator.ArgumentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

/**
 * @author Ivan Kaptue
 */
@RequiredArgsConstructor
@Service
public class StoreManager {

  private final ArgumentValidator argumentValidator;
  private final StoreMapper storeMapper;
  private final StorePersistenceService storePersistenceService;

  public Page<Store> getAll(int page, int size) {
    argumentValidator.validate(page, size);
    var pagedStore = storePersistenceService.findAll(page - 1, size);
    var stores = storeMapper.mapFromStoreEntities(pagedStore.getContent());
    return new PageImpl<>(stores, pagedStore.getPageable(), pagedStore.getTotalElements());
  }
}
