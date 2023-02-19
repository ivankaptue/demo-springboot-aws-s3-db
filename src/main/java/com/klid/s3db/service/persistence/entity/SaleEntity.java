package com.klid.s3db.service.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Ivan Kaptue
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "s3_sales")
@Entity
public class SaleEntity implements Serializable {

  @Id
  @Column(name = "id", unique = true)
  private String id;

  @Column(name = "product", nullable = false)
  private String product;

  @Column(name = "quantity", nullable = false)
  private int quantity;

  @Column(name = "price", nullable = false)
  private BigDecimal price;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private StoreEntity storeEntity;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SaleEntity saleEntity)) return false;

    return getId().equals(saleEntity.getId());
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }

  @Override
  public String toString() {
    return "SaleEntity{" +
      "id='" + id + '\'' +
      ", product='" + product + '\'' +
      ", quantity=" + quantity +
      ", price=" + price +
      ", storeEntity=" + storeEntity +
      '}';
  }
}
