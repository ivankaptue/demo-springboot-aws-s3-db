package com.klid.s3db.data;

import com.klid.s3db.model.dto.Store;
import com.klid.s3db.model.enums.StatusEnum;
import com.klid.s3db.service.persistence.entity.StoreEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class StoreBuilder {

  public static List<Store> createStores(int number) {
    return IntStream.range(1, number + 1)
      .mapToObj(index -> createStore(UUID.randomUUID(), "Maxi " + index, StatusEnum.PENDING))
      .toList();
  }

  public static Store createStore(UUID id, String name, StatusEnum status) {
    return new Store(id.toString(), name, status);
  }

  public static List<StoreEntity> createTenStoreEntities() {
    return IntStream.range(1, 11)
      .mapToObj(index -> {
        var store = createStoreEntity();
        store.setName(String.format("Maxi %s", index));
        return store;
      }).toList();
  }

  public static StoreEntity createStoreEntity() {
    return StoreEntity.builder()
      .id(UUID.randomUUID().toString())
      .name("MAXI")
      .status(StatusEnum.PENDING)
      .build();
  }
}
