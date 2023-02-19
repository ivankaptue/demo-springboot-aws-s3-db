package com.klid.s3db.service.mapper;

import com.klid.s3db.model.dto.Sale;
import com.klid.s3db.service.persistence.entity.SaleEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SaleMapper {

  Sale mapFromSaleEntity(SaleEntity saleEntity);

  default List<Sale> mapFromSaleEntities(List<SaleEntity> saleEntities) {
    return saleEntities.stream()
      .map(this::mapFromSaleEntity)
      .toList();
  }
}
