package io.github.ctorressoftware.domain.model;

import io.github.ctorressoftware.domain.exception.InvalidExpectationException;

/* TODO: Support explicit null expectation values.
   Distinguish between an omitted 'value' property and 'value: null' in YAML,
   so EQUALS/NOT_EQUALS can validate explicit null values without conflicting
   with operators such as EXISTS/NOT_EXISTS. */

public record BodyExpectation(
        String path,
        ExpectationOperator operator,
        Object expectedValue
) {
    public BodyExpectation {
        if (path == null || path.isBlank()) {
            throw new InvalidExpectationException("Expectation path cannot be blank");
        }

        if (operator == null) {
            throw new InvalidExpectationException("Expectation operator cannot be null");
        }

        if (operator.requiresValue() && expectedValue == null) {
            throw new InvalidExpectationException("Operator '%s' requires a value".formatted(operator));
        }

        if (!operator.requiresValue() && expectedValue != null) {
            throw new InvalidExpectationException("Operator '%s' does not accept a value".formatted(operator));
        }
    }
}