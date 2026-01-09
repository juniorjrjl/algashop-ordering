package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import lombok.Builder;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessage.VALIDATION_ERROR_FULL_NAME_IS_NULL_OR_BLANK;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

public class Customer implements AggregateRoot<CustomerId> {

    private CustomerId id;
    private FullName fullName;
    private BirthDate birthDate;
    private Email email;
    private Phone phone;
    private Document document;
    private Boolean promotionNotificationsAllowed;
    private Boolean archived;
    private OffsetDateTime registeredAt;
    private OffsetDateTime archivedAt;
    private LoyaltyPoints loyaltyPoints;
    private Address address;
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
                    final OffsetDateTime archivedAt,
                    final LoyaltyPoints loyaltyPoints,
                    final Address address,
                    final Boolean archived,
                     final Long version) {
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
        return new Customer(
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
    }

    public Address address() {
        return address;
    }

    public LoyaltyPoints loyaltyPoints() {
        return loyaltyPoints;
    }

    public OffsetDateTime archivedAt() {
        return archivedAt;
    }

    public OffsetDateTime registeredAt() {
        return registeredAt;
    }

    public Boolean isArchived() {
        return archived;
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
        if (isNull(loyaltyPointsAdded) || loyaltyPointsAdded.value() <= 0){
            throw new IllegalArgumentException();
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
    }

    public void enablePromotionNotifications(){
        this.setPromotionNotificationsAllowed(true);
    }

    public void disablePromotionNotifications(){
        this.setPromotionNotificationsAllowed(false);
    }

    public void changeFullName(final FullName fullName) {
        this.setFullName(fullName);
    }

    public void changeEmail(final Email email) {
        this.setEmail(email);
    }

    public void changePhone(final Phone phone) {
        this.setPhone(phone);
    }

    public void changeAddress(final Address address) {
        this.setAddress(address);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(id, customer.id);
    }

    public Long version(){
        return this.version;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private void setId(final CustomerId id) {
        this.verifyIfChangeable();
        this.id = requireNonNull(id);
    }

    private void setFullName(final FullName fullName) {
        this.verifyIfChangeable();
        if (isNull(fullName)){
            throw new IllegalArgumentException(VALIDATION_ERROR_FULL_NAME_IS_NULL_OR_BLANK);
        }
        this.fullName = fullName;
    }

    private void setBirthDate(final BirthDate birthDate) {
        this.verifyIfChangeable();
        if (isNull(birthDate)) {
            this.birthDate = null;
            return;
        }
        this.birthDate = birthDate;
    }

    private void setEmail(final Email email) {
        this.verifyIfChangeable();
        this.email = email;
    }

    private void setPhone(final Phone phone) {
        this.verifyIfChangeable();
        this.phone = requireNonNull(phone);
    }

    private void setDocument(final Document document) {
        this.verifyIfChangeable();
        this.document = requireNonNull(document);
    }

    private void setPromotionNotificationsAllowed(final Boolean promotionNotificationsAllowed) {
        this.verifyIfChangeable();
        this.promotionNotificationsAllowed = requireNonNull(promotionNotificationsAllowed);
    }

    private void setArchived(final Boolean archived) {
        this.verifyIfChangeable();
        this.archived = requireNonNull(archived);
    }

    private void setRegisteredAt(final OffsetDateTime registeredAt) {
        this.verifyIfChangeable();
        this.registeredAt = requireNonNull(registeredAt);
    }

    private void setArchivedAt(final OffsetDateTime archivedAt) {
        this.verifyIfChangeable();
        this.archivedAt = archivedAt;
    }

    private void setLoyaltyPoints(final LoyaltyPoints loyaltyPoints) {
        this.verifyIfChangeable();
        this.loyaltyPoints = requireNonNull(loyaltyPoints);
    }

    private void setAddress(final Address address) {
        this.verifyIfChangeable();
        this.address = address;
    }

    private void verifyIfChangeable() {
        if (nonNull(this.isArchived()) && this.isArchived()) {
            throw new CustomerArchivedException();
        }
    }
}
