package com.algaworks.algashop.ordering.infrastructure.persistence.embeddable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RecipientEmbeddable {

    private String firstName;
    private String lastName;
    private String document;
    private String phone;
}
