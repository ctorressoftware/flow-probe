package io.github.ctorressoftware.infrastructure.persistence.exception;

public class CredentialsStorageException extends RuntimeException {
    public CredentialsStorageException(String message) {
        super(message);
    }

    public CredentialsStorageException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
