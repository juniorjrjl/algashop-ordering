package com.algaworks.algashop.ordering.utility;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.DisplayNameGenerator;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomDisplayNameGenerator extends DisplayNameGenerator.Standard {

    private static final List<String> BDD_KEYWORDS = Arrays.asList("given", "when", "then");
    private static final String SHOULD_KEYWORD = "should";

    @NullMarked
    @Override
    public String generateDisplayNameForMethod(final List<Class<?>> enclosingClasses,
                                               final Class<?> testClass,
                                               final Method testMethod) {
        return transformName(testMethod.getName());
    }

    @NullMarked
    @Override
    public String generateDisplayNameForMethod(final Class<?> testClass,
                                               final Method testMethod) {
        return transformName(testMethod.getName());
    }

    private String transformName(final String name) {
        final String[] words = name.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");

        final List<String> lowerWords = Arrays.stream(words)
                .map(String::toLowerCase)
                .toList();

        final boolean hasBddKeywords = lowerWords.stream().anyMatch(BDD_KEYWORDS::contains);
        final boolean hasShould = lowerWords.contains(SHOULD_KEYWORD);

        return Arrays.stream(words)
                .map(word -> {
                    final var lower = word.toLowerCase();

                    if (hasBddKeywords) {
                        if (BDD_KEYWORDS.contains(lower)) {
                            return lower.toUpperCase();
                        }
                        return word;
                    }

                    if (hasShould && lower.equals(SHOULD_KEYWORD)) {
                        return lower.toUpperCase();
                    }

                    return word;
                })
                .collect(Collectors.joining(" "));
    }

}