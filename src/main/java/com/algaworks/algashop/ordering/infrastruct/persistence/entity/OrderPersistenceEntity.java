package com.algaworks.algashop.ordering.infrastruct.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ORDERS")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderPersistenceEntity {

    @Id
    private Long id;
    private UUID customerId;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private String orderStatus;
    private String paymentMethod;
    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof OrderPersistenceEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
