package io.github.ctorressoftware.infrastructure.persistence.exception;

public class InaccessibleCredentialsException extends CredentialsStorageException {
    public InaccessibleCredentialsException(String storageType) {
        super("Credentials are inaccessible for the storage type: " + storageType);
    }
}
