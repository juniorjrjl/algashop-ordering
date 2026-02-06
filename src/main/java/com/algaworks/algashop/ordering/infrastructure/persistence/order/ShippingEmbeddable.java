package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.infrastructure.persistence.common.AddressEmbeddable;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ShippingEmbeddable {

    private BigDecimal cost;
    private LocalDate expectedDate;
    @Embedded
    private AddressEmbeddable address;
    @Embedded
    private RecipientEmbeddable recipient;

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ShippingEmbeddable that)) return false;
        return Objects.equals(cost, that.cost) &&
                Objects.equals(expectedDate, that.expectedDate) &&
                Objects.equals(address, that.address) &&
                Objects.equals(recipient, that.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost, expectedDate, address, recipient);
    }
}
