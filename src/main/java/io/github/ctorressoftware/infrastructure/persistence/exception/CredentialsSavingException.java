package io.github.ctorressoftware.infrastructure.persistence.exception;

public class CredentialsSavingException extends CredentialsStorageException {
    public CredentialsSavingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
