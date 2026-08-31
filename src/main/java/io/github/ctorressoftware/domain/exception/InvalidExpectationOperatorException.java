package io.github.ctorressoftware.domain.exception;

public class InvalidExpectationOperatorException extends RuntimeException {
    public InvalidExpectationOperatorException(String value) {
        super("Operator not supported: " + value);
    }
}
