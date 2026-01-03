package com.algaworks.algashop.ordering.domain.model.exception;

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

    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_ITEMS
            = "Order %s cannot be placed, it has no items";

    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO
            = "Order %s cannot be placed, it has no shipping info";

    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO
            = "Order %s cannot be placed, it has no billing info";

    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD
            = "Order %s cannot be placed, it has no payment method";

    public static final String ERROR_ORDER_DOES_NOT_CONTAIN_ITEM
            = "Order %s does not contain item %s";

    public static final String ERROR_PRODUCT_IS_OUT_OF_STOCK
            = "Product %s is out of stock";

    public static final String ORDER_NOT_ALLOW_CHANGES_EXCEPTION
            = "Order %s cannot be changed, it is in order status '%s'";

    public static final String QUANTITY_LESS_THAN
            = "Quantity must not be less than %s";

    public static final String ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM
            = "Shopping cart %s does not contain item %s";

    public static final String ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT
            = "Shopping cart %s does not contain product %s";

}
