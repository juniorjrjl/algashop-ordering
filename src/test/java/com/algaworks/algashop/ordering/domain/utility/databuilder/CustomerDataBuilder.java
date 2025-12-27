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

    private static final CustomFaker customFaker = new CustomFaker();

    @With
    private Supplier<CustomerId> id = CustomerId::new;
    @With
    private Supplier<FullName> fullName = ()  -> customFaker.valueObject().fullName();
    @With
    private Supplier<BirthDate> birthDate = ()  -> customFaker.valueObject().birthDate();
    @With
    private Supplier<Email> email = ()  -> customFaker.valueObject().email();
    @With
    private Supplier<Phone> phone  = ()  -> customFaker.valueObject().phone();
    @With
    private Supplier<Document> document = ()  -> customFaker.valueObject().document();
    @With
    private Supplier<Boolean> promotionNotificationsAllowed = () -> customFaker.bool().bool();
    @With
    private Supplier<Boolean> archived = () -> customFaker.bool().bool();
    @With
    private Supplier<OffsetDateTime> registeredAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> archivedAt = () -> null;
    @With
    private Supplier<LoyaltyPoints> loyaltyPoints = () -> customFaker.valueObject().loyaltyPoints(100, 9999);
    @With
    private Supplier<Address> address = ()  -> customFaker.valueObject().address();

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
