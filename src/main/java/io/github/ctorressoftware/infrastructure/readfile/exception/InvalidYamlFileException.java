package io.github.ctorressoftware.infrastructure.readfile.exception;

public class InvalidYamlFileException extends RuntimeException {
    public InvalidYamlFileException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
