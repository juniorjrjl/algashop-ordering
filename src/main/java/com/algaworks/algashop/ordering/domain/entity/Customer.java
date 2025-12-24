package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.valueobject.Address;
import com.algaworks.algashop.ordering.domain.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.Document;
import com.algaworks.algashop.ordering.domain.valueobject.Email;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.valueobject.Phone;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Objects;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessage.VALIDATION_ERROR_FULL_NAME_IS_NULL_OR_BLANK;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class Customer {

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
                    final Boolean archived) {
        setId(id);
        setFullName(fullName);
        setBirthDate(birthDate);
        setEmail(email);
        setPhone(phone);
        setDocument(document);
        setPromotionNotificationsAllowed(promotionNotificationsAllowed);
        setRegisteredAt(registeredAt);
        setArchivedAt(archivedAt);
        setLoyaltyPoints(loyaltyPoints);
        setAddress(address);
        setArchived(archived);
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
                false
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
        verifyIfChangeable();
        if (isNull(loyaltyPointsAdded) || loyaltyPointsAdded.value() <= 0){
            throw new IllegalArgumentException();
        }
        setLoyaltyPoints(loyaltyPoints().add(loyaltyPointsAdded));
    }

    public void archive(){
        verifyIfChangeable();
        setArchivedAt(OffsetDateTime.now());
        setFullName(FullName.ANONYMOUS);
        setPhone(Phone.ANONYMOUS);
        setDocument(Document.ANONYMOUS);
        setEmail(Email.ANONYMOUS);
        setBirthDate(null);
        setPromotionNotificationsAllowed(false);
        this.setAddress(address().toBuilder()
                        .number("Anonymous")
                        .complement(null)
                .build());
        setArchived(true);
    }

    public void enablePromotionNotifications(){
        setPromotionNotificationsAllowed(true);
    }

    public void disablePromotionNotifications(){
        setPromotionNotificationsAllowed(false);
    }

    public void changeFullName(final FullName fullName) {
        setFullName(fullName);
    }

    public void changeEmail(final Email email) {
        setEmail(email);
    }

    public void changePhone(final Phone phone) {
        setPhone(phone);
    }

    public void changeAddress(final Address address) {
        setAddress(address);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private void setId(final CustomerId id) {
        verifyIfChangeable();
        this.id = requireNonNull(id);
    }

    private void setFullName(final FullName fullName) {
        verifyIfChangeable();
        if (isNull(fullName)){
            throw new IllegalArgumentException(VALIDATION_ERROR_FULL_NAME_IS_NULL_OR_BLANK);
        }
        this.fullName = fullName;
    }

    private void setBirthDate(final BirthDate birthDate) {
        verifyIfChangeable();
        if (isNull(birthDate)) {
            this.birthDate = null;
            return;
        }
        this.birthDate = birthDate;
    }

    private void setEmail(final Email email) {
        verifyIfChangeable();
        this.email = email;
    }

    private void setPhone(final Phone phone) {
        verifyIfChangeable();
        this.phone = requireNonNull(phone);
    }

    private void setDocument(final Document document) {
        verifyIfChangeable();
        this.document = requireNonNull(document);
    }

    private void setPromotionNotificationsAllowed(final Boolean promotionNotificationsAllowed) {
        verifyIfChangeable();
        this.promotionNotificationsAllowed = requireNonNull(promotionNotificationsAllowed);
    }

    private void setArchived(final Boolean archived) {
        verifyIfChangeable();
        this.archived = requireNonNull(archived);
    }

    private void setRegisteredAt(final OffsetDateTime registeredAt) {
        verifyIfChangeable();
        this.registeredAt = requireNonNull(registeredAt);
    }

    public void setArchivedAt(final OffsetDateTime archivedAt) {
        verifyIfChangeable();
        this.archivedAt = archivedAt;
    }

    private void setLoyaltyPoints(final LoyaltyPoints loyaltyPoints) {
        verifyIfChangeable();
        this.loyaltyPoints = requireNonNull(loyaltyPoints);
    }

    private void setAddress(final Address address) {
        verifyIfChangeable();
        this.address = address;
    }

    private void verifyIfChangeable() {
        if (nonNull(isArchived()) && isArchived()) {
            throw new CustomerArchivedException();
        }
    }
}
