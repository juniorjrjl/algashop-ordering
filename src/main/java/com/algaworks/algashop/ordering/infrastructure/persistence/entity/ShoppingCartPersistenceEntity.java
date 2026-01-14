package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static java.util.Objects.isNull;

@Entity
@Table(name = "SHOPPING_CARTS")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ShoppingCartPersistenceEntity {

    @Id
    private UUID id;
    @JoinColumn
    @ManyToOne(optional = false)
    private CustomerPersistenceEntity customer;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private OffsetDateTime createdAt;
    @ToString.Exclude
    @OneToMany(mappedBy = "shoppingCart", cascade = ALL,  orphanRemoval = true)
    private Set<ShoppingCartItemPersistenceEntity> items;
    @CreatedBy
    private UUID createdBy;
    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;
    @LastModifiedBy
    private UUID lastModifiedBy;

    @Version
    private Long version;

    public ShoppingCartPersistenceEntity(final UUID id,
                                         final CustomerPersistenceEntity customer,
                                         final BigDecimal totalAmount,
                                         final Integer totalItems,
                                         final OffsetDateTime createdAt,
                                         final Set<ShoppingCartItemPersistenceEntity> items,
                                         final UUID createdBy,
                                         final OffsetDateTime lastModifiedAt,
                                         final UUID lastModifiedBy,
                                         final Long version) {
        this.id = id;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.createdAt = createdAt;
        this.items = items;
        this.createdBy = createdBy;
        this.lastModifiedAt = lastModifiedAt;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
        this.addCartToItems();
    }

    public void addCartToItems() {
        if (isNull(this.items) || this.items.isEmpty()) {
            return;
        }
        this.items.forEach(i -> i.setShoppingCart(this));
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ShoppingCartPersistenceEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
