package com.klid.s3db.service.mapper;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.klid.s3db.data.SaleBuilder.createSaleEntity;
import static org.assertj.core.api.Assertions.assertThat;

class SaleMapperTest {

  private final SaleMapper saleMapper = new SaleMapperImpl();

  @Test
  void shouldMapSaleEntityToSaleDTO() {
    var saleEntity = createSaleEntity();
    saleEntity.setId("08b06522-8899-4a94-afe2-243a59b29237");

    var sale = saleMapper.mapFromSaleEntity(saleEntity);

    assertThat(sale.id()).isEqualTo("08b06522-8899-4a94-afe2-243a59b29237");
    assertThat(sale.product()).isEqualTo("Milk");
    assertThat(sale.quantity()).isEqualTo(3);
    assertThat(sale.price()).isEqualTo(new BigDecimal("10.00"));
  }

  @Test
  void shouldMapSaleEntityListToSaleDTOList() {
    var saleEntities = List.of(createSaleEntity(), createSaleEntity());

    var saleDTOList = saleMapper.mapFromSaleEntities(saleEntities);

    assertThat(saleDTOList).hasSize(2);
  }

  @Test
  void shouldReturnNullWhenNullEntry() {
    var result = saleMapper.mapFromSaleEntity(null);

    assertThat(result).isNull();
  }
}
