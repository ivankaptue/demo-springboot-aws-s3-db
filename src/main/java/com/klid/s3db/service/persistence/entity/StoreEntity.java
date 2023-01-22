package com.klid.s3db.service.persistence.entity;

import com.klid.s3db.model.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * @author Ivan Kaptue
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "s3_stores")
@Entity
public class StoreEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusEnum status;

    @OneToMany(mappedBy = "storeEntity")
    private Set<SaleEntity> saleEntities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoreEntity storeEntity)) return false;

        return Objects.equals(getId(), storeEntity.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
