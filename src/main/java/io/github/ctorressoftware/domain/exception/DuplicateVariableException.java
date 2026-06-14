package io.github.ctorressoftware.domain.exception;

public class DuplicateVariableException extends RuntimeException {
    public DuplicateVariableException(String variableName) {
        super("Variable already exists in context: " + variableName);
    }
}
