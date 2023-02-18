package com.klid.s3db.service;

import com.klid.s3db.exception.StoreEntityNotFoundException;
import com.klid.s3db.model.dto.Sale;
import com.klid.s3db.service.mapper.SaleMapper;
import com.klid.s3db.service.persistence.SalePersistenceService;
import com.klid.s3db.service.persistence.StorePersistenceService;
import com.klid.s3db.service.persistence.entity.StoreEntity;
import com.klid.s3db.service.validator.ArgumentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SaleManager {

    private final ArgumentValidator argumentValidator;
    private final SaleMapper saleMapper;
    private final SalePersistenceService salePersistenceService;
    private final StorePersistenceService storePersistenceService;

    public Page<Sale> getAll(int page, int size, String storeId) {
        argumentValidator.validate(page, size, storeId);
        var storeEntity = findStoreEntity(UUID.fromString(storeId));
        var pagedStore = salePersistenceService.findAll(page - 1, size, storeEntity);
        var stores = saleMapper.mapFromSaleEntities(pagedStore.getContent());
        return new PageImpl<>(stores, pagedStore.getPageable(), pagedStore.getTotalElements());
    }

    @NonNull
    private StoreEntity findStoreEntity(UUID storeId) {
        return storePersistenceService.findById(storeId)
            .orElseThrow(() -> new StoreEntityNotFoundException(storeId));
    }
}
