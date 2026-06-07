package io.github.ctorressoftware.exceptions;

public class NoDefinedStepsException extends RuntimeException {
    public NoDefinedStepsException() {
        super("No defined steps in the specified file");
    }
}
