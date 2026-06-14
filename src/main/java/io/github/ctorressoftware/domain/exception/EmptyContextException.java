package io.github.ctorressoftware.domain.exception;

public class EmptyContextException extends RuntimeException {
    public EmptyContextException() {
        super("Context is empty. No variables are available.");
    }
}
