package com.klid.s3db.service.mapper;

import com.klid.s3db.model.dto.Store;
import com.klid.s3db.service.persistence.entity.StoreEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * @author Ivan Kaptue
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StoreMapper {

  Store mapFromStoreEntity(final StoreEntity storeEntity);

  default List<Store> mapFromStoreEntities(@NonNull final List<StoreEntity> storeEntities) {
    return storeEntities.stream()
      .map(this::mapFromStoreEntity)
      .toList();
  }
}
