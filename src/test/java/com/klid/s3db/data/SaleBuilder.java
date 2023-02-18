package com.klid.s3db.data;

import com.klid.s3db.model.dto.Sale;
import com.klid.s3db.service.persistence.entity.SaleEntity;
import com.klid.s3db.service.persistence.entity.StoreEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class SaleBuilder {

    public static List<SaleEntity> createSaleEntities(int number) {
        return IntStream.range(1, number + 1)
            .mapToObj(index -> {
                var sale = createSaleEntity();
                sale.setProduct(String.format("Meal %s", index));
                return sale;
            }).toList();
    }

    public static SaleEntity createSaleEntity() {
        return SaleEntity.builder()
            .id(UUID.randomUUID().toString())
            .storeEntity(new StoreEntity())
            .product("Milk")
            .price(new BigDecimal("10.00"))
            .quantity(3)
            .build();
    }

    public static List<Sale> createSales(int number) {
        return IntStream.range(1, number + 1)
            .mapToObj(index -> createSale(UUID.randomUUID(), "Meal " + index, index, BigDecimal.TEN.add(BigDecimal.valueOf(index))))
            .toList();
    }

    public static Sale createSale(UUID id, String product, int quantity, BigDecimal price) {
        return new Sale(id.toString(), product, quantity, price);
    }
}
