package io.github.ctorressoftware.domain.model;

import io.github.ctorressoftware.domain.exception.InvalidExpectationOperatorException;

import java.util.Arrays;

public enum ExpectationOperator {
    EQUALS("equals", true),
    NOT_EQUALS("notEquals", true);

    private final String yamlValue;
    private final boolean requiresValue;

    ExpectationOperator(String yamlValue, boolean requiresValue) {
        this.yamlValue = yamlValue;
        this.requiresValue = requiresValue;
    }

    public static ExpectationOperator from(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidExpectationOperatorException(value);
        }

        return Arrays.stream(values())
                .filter(operator ->
                        operator.yamlValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidExpectationOperatorException(value));
    }

    public boolean requiresValue() {
        return requiresValue;
    }
}
