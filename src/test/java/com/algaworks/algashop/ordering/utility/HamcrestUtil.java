package com.algaworks.algashop.ordering.utility;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class HamcrestUtil {

    public static Matcher<String> sameInstant(final OffsetDateTime expected) {
        return new TypeSafeMatcher<>() {
            @Override
            protected boolean matchesSafely(String item) {
                return OffsetDateTime.parse(item).truncatedTo(ChronoUnit.MILLIS)
                        .equals(expected.truncatedTo(ChronoUnit.MILLIS));
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("same instant as " + expected);
            }
        };
    }

}
