package io.github.ctorressoftware.domain.model;

import io.github.ctorressoftware.domain.exception.InvalidExpectationException;

public record BodyExpectation(
        String path,
        ExpectationOperator operator,
        Object expectedValue
) {
    public BodyExpectation {
        if (path == null || path.isBlank()) {
            throw new InvalidExpectationException(
                    "Expectation path cannot be blank"
            );
        }

        if (operator == null) {
            throw new InvalidExpectationException(
                    "Expectation operator cannot be null"
            );
        }

        if (operator.requiresValue() && expectedValue == null) {
            throw new InvalidExpectationException(
                    "Operator '%s' requires a value"
                            .formatted(operator)
            );
        }

        if (!operator.requiresValue() && expectedValue != null) {
            throw new InvalidExpectationException(
                    "Operator '%s' does not accept a value"
                            .formatted(operator)
            );
        }
    }
}