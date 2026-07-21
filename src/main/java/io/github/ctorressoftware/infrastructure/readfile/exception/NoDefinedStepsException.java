package io.github.ctorressoftware.infrastructure.readfile.exception;

public class NoDefinedStepsException extends RuntimeException {
    public NoDefinedStepsException(String filePath) {
        super("No defined steps in the specified file: " + filePath);
    }
}
