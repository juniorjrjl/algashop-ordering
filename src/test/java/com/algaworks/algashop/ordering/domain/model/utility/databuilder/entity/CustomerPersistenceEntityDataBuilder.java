package com.algaworks.algashop.ordering.domain.model.utility.databuilder.entity;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.utility.IdGenerator;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.embeddable.AddressEmbeddableDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class CustomerPersistenceEntityDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<UUID> id = IdGenerator::generateUUID;
    @With
    private Supplier<String> firstName = () -> customFaker.name().firstName();
    @With
    private Supplier<String> lastName =  () -> customFaker.name().lastName();
    @With
    private Supplier<LocalDate> birthDate = () -> LocalDate.ofInstant(customFaker.timeAndDate().future(), ZoneId.of("UTC"));;
    @With
    private Supplier<String> email = () -> customFaker.internet().emailAddress();
    @With
    private Supplier<String> phone = () -> customFaker.phoneNumber().cellPhone();
    @With
    private Supplier<String> document = () -> customFaker.cpf().valid();
    @With
    private Supplier<Boolean> promotionNotificationsAllowed = () -> customFaker.bool().bool();
    @With
    private Supplier<Boolean> archived = () -> customFaker.bool().bool();
    @With
    private Supplier<OffsetDateTime> registeredAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> archivedAt = OffsetDateTime::now;
    @With
    private Supplier<Integer> loyaltyPoints = () -> customFaker.number().positive();
    @With
    private Supplier<AddressEmbeddable> address = () -> AddressEmbeddableDataBuilder.builder().build();
    @With
    private Supplier<UUID> createdBy = IdGenerator::generateUUID;
    @With
    private Supplier<OffsetDateTime> lastModifiedAt = OffsetDateTime::now;
    @With
    private Supplier<UUID> lastModifiedBy = IdGenerator::generateUUID;

    public static CustomerPersistenceEntityDataBuilder builder() {
        return new CustomerPersistenceEntityDataBuilder();
    }

    public CustomerPersistenceEntity build() {
        return new CustomerPersistenceEntity(
                id.get(),
                firstName.get(),
                lastName.get(),
                birthDate.get(),
                email.get(),
                phone.get(),
                document.get(),
                promotionNotificationsAllowed.get(),
                archived.get(),
                registeredAt.get(),
                archivedAt.get(),
                loyaltyPoints.get(),
                address.get(),
                createdBy.get(),
                lastModifiedAt.get(),
                lastModifiedBy.get(),
                null
        );
    }

}
