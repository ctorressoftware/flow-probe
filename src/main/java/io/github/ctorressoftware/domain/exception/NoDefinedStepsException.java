package io.github.ctorressoftware.domain.exception;

public class NoDefinedStepsException extends RuntimeException {
    public NoDefinedStepsException() {
        super("No defined steps in the specified file");
    }
}
