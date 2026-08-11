package io.github.ctorressoftware.infrastructure.persistence.exception;

public class InvalidStoredCredentialsException extends CredentialsStorageException {
    public InvalidStoredCredentialsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
