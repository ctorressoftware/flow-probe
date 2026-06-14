package io.github.ctorressoftware.domain.exception;

public class MissingVariableException extends RuntimeException {
    public MissingVariableException(String variableName) {
        super("Variable not found in context: " + variableName);
    }
}
