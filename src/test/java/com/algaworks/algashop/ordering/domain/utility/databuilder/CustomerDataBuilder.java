package com.algaworks.algashop.ordering.domain.utility.databuilder;

import com.algaworks.algashop.ordering.domain.entity.Customer;
import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.valueobject.Address;
import com.algaworks.algashop.ordering.domain.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.Document;
import com.algaworks.algashop.ordering.domain.valueobject.Email;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.valueobject.Phone;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.OffsetDateTime;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class CustomerDataBuilder {

    private static final CustomFaker faker = new CustomFaker();

    @With
    private Supplier<CustomerId> id = () -> faker.valueObject().customerId();
    @With
    private Supplier<FullName> fullName = ()  -> faker.valueObject().fullName();
    @With
    private Supplier<BirthDate> birthDate = ()  -> faker.valueObject().birthDate();
    @With
    private Supplier<Email> email = ()  -> faker.valueObject().email();
    @With
    private Supplier<Phone> phone  = ()  -> faker.valueObject().phone();
    @With
    private Supplier<Document> document = ()  -> faker.valueObject().document();
    @With
    private Supplier<Boolean> promotionNotificationsAllowed = () -> faker.bool().bool();
    @With
    private Supplier<Boolean> archived = () -> faker.bool().bool();
    @With
    private Supplier<OffsetDateTime> registeredAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> archivedAt = () -> null;
    @With
    private Supplier<LoyaltyPoints> loyaltyPoints = ()  -> faker.valueObject().loyaltyPoints();
    @With
    private Supplier<Address> address = ()  -> faker.valueObject().address();

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

}
