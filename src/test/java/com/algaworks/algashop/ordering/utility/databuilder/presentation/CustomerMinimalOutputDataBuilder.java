package com.algaworks.algashop.ordering.utility.databuilder.presentation;

import com.algaworks.algashop.ordering.application.order.query.CustomerMinimalOutput;
import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class CustomerMinimalOutputDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<UUID> id = UUID::randomUUID;
    @With
    private Supplier<String> firstName = () -> customFaker.name().firstName();
    @With
    private Supplier<String> lastName = () -> customFaker.name().lastName();
    @With
    private Supplier<String> email = ()  -> customFaker.internet().emailAddress();
    @With
    private Supplier<String> phone  = ()  -> customFaker.phoneNumber().cellPhone();
    @With
    private Supplier<String> document = ()  -> customFaker.cpf().valid(true);

    public static CustomerMinimalOutputDataBuilder builder() {
        return new CustomerMinimalOutputDataBuilder();
    }


    public CustomerMinimalOutput build(){
        return CustomerMinimalOutput.builder()
                .id(id.get())
                .firstName(firstName.get())
                .lastName(lastName.get())
                .email(email.get())
                .phone(phone.get())
                .document(document.get())
                .build();
    }

}
