package com.algaworks.algashop.ordering.utility;

import net.datafaker.providers.base.AbstractProvider;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class JsonProvider extends AbstractProvider<CustomFaker> {

    protected JsonProvider(final CustomFaker faker) {
        super(faker);
    }

    public String toJsonDate(final LocalDate date) {
        return date.format(ISO_DATE);
    }

    public String toJsonDateTime(final OffsetDateTime date) {
        return date.format(ISO_OFFSET_DATE_TIME);
    }

}
