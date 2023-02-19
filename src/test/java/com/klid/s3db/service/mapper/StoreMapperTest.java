package com.klid.s3db.service.mapper;

import com.klid.s3db.model.enums.StatusEnum;
import com.klid.s3db.service.persistence.entity.StoreEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Ivan Kaptue
 */
class StoreMapperTest {

  private final StoreMapper storeMapper = new StoreMapperImpl();

  @Test
  void shouldConvertStoreEntityToStoreDTO() {
    var storeEntity = createStoreEntity();

    var storeDTO = storeMapper.mapFromStoreEntity(storeEntity);

    assertThat(storeDTO.id()).isEqualTo("08b06522-8899-4a94-afe2-243a59b29237");
    assertThat(storeDTO.name()).isEqualTo("Maxi");
    assertThat(storeDTO.status()).isEqualTo(StatusEnum.PENDING);
  }

  @Test
  void shouldConvertStoreEntityListToStoreDTOList() {
    var storeEntities = List.of(createStoreEntity(), createStoreEntity());

    var storeDTOList = storeMapper.mapFromStoreEntities(storeEntities);

    assertThat(storeDTOList).hasSize(2);
  }

  @Test
  void shouldReturnNullWhenNullEntry() {
    var result = storeMapper.mapFromStoreEntity(null);

    assertThat(result).isNull();
  }

  private StoreEntity createStoreEntity() {
    return StoreEntity.builder()
      .id("08b06522-8899-4a94-afe2-243a59b29237")
      .name("Maxi")
      .status(StatusEnum.PENDING)
      .build();
  }
}
