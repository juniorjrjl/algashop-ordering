package com.algaworks.algashop.ordering.utility.databuilder.domain;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class CustomerDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<CustomerId> id = CustomerId::new;
    @With
    private Supplier<FullName> fullName = ()  -> customFaker.common().fullName();
    @With
    private Supplier<BirthDate> birthDate = ()  -> customFaker.customer().birthDate();
    @With
    private Supplier<Email> email = ()  -> customFaker.common().email();
    @With
    private Supplier<Phone> phone  = ()  -> customFaker.common().phone();
    @With
    private Supplier<Document> document = ()  -> customFaker.common().document();
    @With
    private Supplier<Boolean> promotionNotificationsAllowed = () -> customFaker.bool().bool();
    @With
    private Supplier<Boolean> archived = () -> customFaker.bool().bool();
    @With
    private Supplier<OffsetDateTime> registeredAt = OffsetDateTime::now;
    @With
    private Supplier<@Nullable OffsetDateTime> archivedAt = () -> null;
    @With
    private Supplier<LoyaltyPoints> loyaltyPoints = () -> customFaker.customer().loyaltyPoints(100, 9999);
    @With
    private Supplier<Address> address = ()  -> customFaker.common().address();

    public static CustomerDataBuilder builder() {
        return new CustomerDataBuilder();
    }


    public Customer buildExisting(){
        return Customer.existing()
                .id(id.get())
                .fullName(fullName.get())
                .birthDate(birthDate.get())
                .email(email.get())
                .phone(phone.get())
                .document(document.get())
                .promotionNotificationsAllowed(promotionNotificationsAllowed.get())
                .archived(archived.get())
                .registeredAt(registeredAt.get())
                .archivedAt(archivedAt.get())
                .loyaltyPoints(loyaltyPoints.get())
                .address(address.get())
                .build();
    }

    public Customer buildNew(){
        return Customer.brandNew()
                .fullName(fullName.get())
                .birthDate(birthDate.get())
                .email(email.get())
                .phone(phone.get())
                .document(document.get())
                .promotionNotificationsAllowed(promotionNotificationsAllowed.get())
                .address(address.get())
                .build();
    }

    public Set<Customer> buildExistingList(final int amount){
        return Stream.generate(this::buildExisting)
                .limit(amount).collect(Collectors.toSet());
    }

}
