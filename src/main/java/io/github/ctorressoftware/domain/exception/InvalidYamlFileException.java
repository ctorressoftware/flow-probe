package io.github.ctorressoftware.domain.exception;

public class InvalidYamlFileException extends RuntimeException {
    public InvalidYamlFileException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
