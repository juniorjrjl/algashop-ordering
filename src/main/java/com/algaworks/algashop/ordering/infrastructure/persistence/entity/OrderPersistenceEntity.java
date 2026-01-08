package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static java.util.Objects.isNull;

@Entity
@Table(name = "ORDERS")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
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

    @CreatedBy
    private UUID createdBy;
    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;
    @LastModifiedBy
    private UUID lastModifiedBy;

    @Version
    private Long version;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "billing_first_name")),
            @AttributeOverride(name = "lastName", column = @Column(name = "billing_last_name")),
            @AttributeOverride(name = "document", column = @Column(name = "billing_document")),
            @AttributeOverride(name = "phone", column = @Column(name = "billing_phone")),
            @AttributeOverride(name = "address.street", column = @Column(name = "billing_address_street")),
            @AttributeOverride(name = "address.number", column = @Column(name = "billing_address_number")),
            @AttributeOverride(name = "address.complement", column = @Column(name = "billing_address_complement")),
            @AttributeOverride(name = "address.neighborhood", column = @Column(name = "billing_address_neighborhood")),
            @AttributeOverride(name = "address.city", column = @Column(name = "billing_address_city")),
            @AttributeOverride(name = "address.state", column = @Column(name = "billing_address_state")),
            @AttributeOverride(name = "address.zipCode", column = @Column(name = "billing_address_zipCode"))
    })
    private BillingEmbeddable billing;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cost", column = @Column(name = "shipping_cost")),
            @AttributeOverride(name = "expectedDate", column = @Column(name = "shipping_expected_date")),
            @AttributeOverride(name = "recipient.firstName", column = @Column(name = "shipping_recipient_first_name")),
            @AttributeOverride(name = "recipient.lastName", column = @Column(name = "shipping_recipient_last_name")),
            @AttributeOverride(name = "recipient.document", column = @Column(name = "shipping_recipient_document")),
            @AttributeOverride(name = "recipient.phone", column = @Column(name = "shipping_recipient_phone")),
            @AttributeOverride(name = "address.street", column = @Column(name = "shipping_address_street")),
            @AttributeOverride(name = "address.number", column = @Column(name = "shipping_address_number")),
            @AttributeOverride(name = "address.complement", column = @Column(name = "shipping_address_complement")),
            @AttributeOverride(name = "address.neighborhood", column = @Column(name = "shipping_address_neighborhood")),
            @AttributeOverride(name = "address.city", column = @Column(name = "shipping_address_city")),
            @AttributeOverride(name = "address.state", column = @Column(name = "shipping_address_state")),
            @AttributeOverride(name = "address.zipCode", column = @Column(name = "shipping_address_zipCode"))
    })
    private ShippingEmbeddable shipping;

    public OrderPersistenceEntity(
            final Long id,
            final UUID customerId,
            final BigDecimal totalAmount,
            final Integer totalItems,
            final String orderStatus,
            final String paymentMethod,
            final OffsetDateTime placedAt,
            final OffsetDateTime paidAt,
            final OffsetDateTime canceledAt,
            final OffsetDateTime readyAt,
            final UUID createdBy,
            final OffsetDateTime lastModifiedAt,
            final UUID lastModifiedBy,
            final Long version,
            final BillingEmbeddable billing,
            final ShippingEmbeddable shipping,
            final Set<OrderItemPersistenceEntity> items) {
        this.id = id;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.placedAt = placedAt;
        this.paidAt = paidAt;
        this.canceledAt = canceledAt;
        this.readyAt = readyAt;
        this.createdBy = createdBy;
        this.lastModifiedAt = lastModifiedAt;
        this.lastModifiedBy = lastModifiedBy;
        this.version = version;
        this.billing = billing;
        this.shipping = shipping;
        this.items = items;
        this.addOrderToItems();
    }

    @ToString.Exclude
    @OneToMany(mappedBy = "order", cascade = ALL,  orphanRemoval = true)
    private Set<OrderItemPersistenceEntity> items = new HashSet<>();

    public void addOrderToItems(){
        if (isNull(this.items) || this.items.isEmpty()) {
            return;
        }
        items.forEach(i -> i.setOrder(this));
    }

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
