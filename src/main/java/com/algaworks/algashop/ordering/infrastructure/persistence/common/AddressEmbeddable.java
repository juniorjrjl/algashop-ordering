package com.algaworks.algashop.ordering.infrastructure.persistence.common;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AddressEmbeddable {

    private String street;
    private String complement;
    private String neighborhood;
    private String number;
    private String city;
    private String state;
    private String zipCode;

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof AddressEmbeddable that)) return false;
        return Objects.equals(street, that.street) &&
                Objects.equals(complement, that.complement) &&
                Objects.equals(neighborhood, that.neighborhood) &&
                Objects.equals(number, that.number) &&
                Objects.equals(city, that.city) &&
                Objects.equals(state, that.state) &&
                Objects.equals(zipCode, that.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, complement, neighborhood, number, city, state, zipCode);
    }
}
