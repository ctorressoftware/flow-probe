package io.github.ctorressoftware.domain.exception;

public class NoDefinedFlowException extends RuntimeException {
    public NoDefinedFlowException() {
        super("No defined flow. Flow is null");
    }
}
