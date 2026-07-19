package io.github.ctorressoftware.domain.exception;

public class NoDefinedStepsException extends RuntimeException {
    public NoDefinedStepsException(String filePath) {
        super("No defined steps in the specified file: " + filePath);
    }
}
