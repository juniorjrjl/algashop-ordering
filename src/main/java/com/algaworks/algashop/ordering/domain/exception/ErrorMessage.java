package com.algaworks.algashop.ordering.domain.exception;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ErrorMessage {

    public static final String VALIDATION_ERROR_EMAIL_IS_INVALID
            = "Email is invalid";

    public static final String VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST
            = "BirthDate must be a past date";

    public static final String VALIDATION_ERROR_FULL_NAME_IS_NULL_OR_BLANK
            = "FullName cannot be null or blank";

    public static final String ERROR_CUSTOMER_ARCHIVED
            = "Customer already archived";

    public static final String ERROR_ORDER_STATUS_CANNOT_BE_CHANGED
            = "Cannot change order %s status from %s to %s";

    public static final String ERROR_ORDER_DELIVERY_DATE_CANNOT_BE_IN_THE_PAST
            = "Order %s expected delivery date cannot be in the past";

    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NONE_ITEMS
            = "Order %s cannot be placed, it has no items";


}
