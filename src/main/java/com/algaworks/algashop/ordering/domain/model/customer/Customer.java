package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.AggregateRoot;
import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.AbstractEventSourceEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

public class Customer extends AbstractEventSourceEntity implements AggregateRoot<CustomerId>{

    private CustomerId id;
    private FullName fullName;
    @Nullable
    private BirthDate birthDate;
    private Email email;
    private Phone phone;
    private Document document;
    private Boolean promotionNotificationsAllowed;
    @Getter
    private boolean archived;
    private OffsetDateTime registeredAt;
    @Nullable
    private OffsetDateTime archivedAt;
    private LoyaltyPoints loyaltyPoints;
    private Address address;
    @Nullable
    @Setter(PRIVATE)
    private Long version;

    @Builder(builderClassName = "ExistingCustomerBuilder", builderMethodName = "existing")
    private Customer(final CustomerId id,
                     final FullName fullName,
                     final BirthDate birthDate,
                     final Email email,
                     final Phone phone,
                     final Document document,
                     final Boolean promotionNotificationsAllowed,
                     final OffsetDateTime registeredAt,
                     @Nullable final OffsetDateTime archivedAt,
                     final LoyaltyPoints loyaltyPoints,
                     final Address address,
                     final boolean archived,
                     @Nullable final Long version) {
        this.setId(id);
        this.setFullName(fullName);
        this.setBirthDate(birthDate);
        this.setEmail(email);
        this.setPhone(phone);
        this.setDocument(document);
        this.setPromotionNotificationsAllowed(promotionNotificationsAllowed);
        this.setRegisteredAt(registeredAt);
        this.setArchivedAt(archivedAt);
        this.setLoyaltyPoints(loyaltyPoints);
        this.setAddress(address);
        this.setArchived(archived);
        this.setVersion(version);
    }

    @Builder(builderClassName = "BrandNewCustomerBuilder", builderMethodName = "brandNew")
    private static Customer createBrandNew(final FullName fullName,
                                           final BirthDate birthDate,
                                           final Email email,
                                           final Phone phone,
                                           final Document document,
                                           final Boolean promotionNotificationsAllowed,
                                           final Address address){
        final var customer = new Customer(
                new CustomerId(),
                fullName,
                birthDate,
                email,
                phone,
                document,
                promotionNotificationsAllowed,
                OffsetDateTime.now(),
                null,
                LoyaltyPoints.ZERO,
                address,
                false,
                null
        );
        final var registeredEvent = new CustomerRegisteredEvent(customer.id(),
                customer.fullName(),
                customer.email(),
                customer.registeredAt());
        customer.publishDomainEvent(registeredEvent);
        return customer;
    }

    public Address address() {
        return address;
    }

    public LoyaltyPoints loyaltyPoints() {
        return loyaltyPoints;
    }

    @Nullable
    public OffsetDateTime archivedAt() {
        return archivedAt;
    }

    public OffsetDateTime registeredAt() {
        return registeredAt;
    }

    public Boolean isPromotionNotificationsAllowed() {
        return promotionNotificationsAllowed;
    }

    public Document document() {
        return document;
    }

    public Phone phone() {
        return phone;
    }

    public Email email() {
        return email;
    }

    @Nullable
    public BirthDate birthDate() {
        return birthDate;
    }

    public FullName fullName() {
        return fullName;
    }

    public CustomerId id() {
        return id;
    }

    public void addLoyaltyPoints(final LoyaltyPoints loyaltyPointsAdded) {
        this.verifyIfChangeable();
        if (loyaltyPointsAdded.equals(LoyaltyPoints.ZERO)) {
            return;
        }
        this.setLoyaltyPoints(loyaltyPoints().add(loyaltyPointsAdded));
    }

    public void archive(){
        this.verifyIfChangeable();
        this.setArchivedAt(OffsetDateTime.now());
        this.setFullName(FullName.ANONYMOUS);
        this.setPhone(Phone.ANONYMOUS);
        this.setDocument(Document.ANONYMOUS);
        this.setEmail(Email.ANONYMOUS);
        this.setBirthDate(null);
        this.setPromotionNotificationsAllowed(false);
        this.setAddress(address().toBuilder()
                        .number("Anonymous")
                        .complement(null)
                .build());
        this.setArchived(true);
        this.publishDomainEvent(new CustomerArchivedEvent(this.id(), requireNonNull(this.archivedAt())));
    }

    public void enablePromotionNotifications(){
        this.verifyIfChangeable();
        this.setPromotionNotificationsAllowed(true);
    }

    public void disablePromotionNotifications(){
        this.verifyIfChangeable();
        this.setPromotionNotificationsAllowed(false);
    }

    public void changeFullName(final FullName fullName) {
        this.verifyIfChangeable();
        this.setFullName(fullName);
    }

    public void changeEmail(final Email email) {
        this.verifyIfChangeable();
        this.setEmail(email);
    }

    public void changePhone(final Phone phone) {
        this.verifyIfChangeable();
        this.setPhone(phone);
    }

    public void changeAddress(final Address address) {
        this.verifyIfChangeable();
        this.setAddress(address);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(id, customer.id);
    }

    @Nullable
    public Long version(){
        return this.version;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private void setId(final CustomerId id) {
        this.id = requireNonNull(id);
    }

    private void setFullName(final FullName fullName) {
        this.fullName = fullName;
    }

    private void setBirthDate(@Nullable final BirthDate birthDate) {
        if (isNull(birthDate)) {
            this.birthDate = null;
            return;
        }
        this.birthDate = birthDate;
    }

    private void setEmail(final Email email) {
        this.email = email;
    }

    private void setPhone(final Phone phone) {
        this.phone = requireNonNull(phone);
    }

    private void setDocument(final Document document) {
        this.document = requireNonNull(document);
    }

    private void setPromotionNotificationsAllowed(final Boolean promotionNotificationsAllowed) {
        this.promotionNotificationsAllowed = requireNonNull(promotionNotificationsAllowed);
    }

    private void setArchived(final boolean archived) {
        this.archived = archived;
    }

    private void setRegisteredAt(final OffsetDateTime registeredAt) {
        this.registeredAt = requireNonNull(registeredAt);
    }

    private void setArchivedAt(@Nullable final OffsetDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    private void setLoyaltyPoints(final LoyaltyPoints loyaltyPoints) {
        this.loyaltyPoints = requireNonNull(loyaltyPoints);
    }

    private void setAddress(final Address address) {
        this.address = address;
    }

    private void verifyIfChangeable() {
        if (this.isArchived()) {
            throw new CustomerArchivedException();
        }
    }
}
