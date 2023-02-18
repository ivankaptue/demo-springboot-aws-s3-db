package com.klid.s3db.service.persistence.repository;

import com.klid.s3db.service.persistence.entity.SaleEntity;
import com.klid.s3db.service.persistence.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity, String> {

    Page<SaleEntity> findAllByStoreEntity(StoreEntity storeEntity, Pageable pageable);
}
