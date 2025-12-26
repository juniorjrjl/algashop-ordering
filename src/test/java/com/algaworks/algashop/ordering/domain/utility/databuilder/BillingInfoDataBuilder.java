package com.algaworks.algashop.ordering.domain.utility.databuilder;

import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.valueobject.Address;
import com.algaworks.algashop.ordering.domain.valueobject.BillingInfo;
import com.algaworks.algashop.ordering.domain.valueobject.Document;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.Phone;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class BillingInfoDataBuilder {

    private static final CustomFaker faker = new CustomFaker();

    @With
    private Supplier<FullName> fullName = () -> faker.valueObject().fullName();
    @With
    private Supplier<Document> document = () -> faker.valueObject().document();
    @With
    private Supplier<Phone> phone = () -> faker.valueObject().phone();
    @With
    private Supplier<Address> address = () -> faker.valueObject().address();

    public static BillingInfoDataBuilder builder() {
        return new BillingInfoDataBuilder();
    }

    public BillingInfo buildNew(){
        return new BillingInfo(
                fullName.get(),
                document.get(),
                phone.get(),
                address.get()
        );
    }

}
