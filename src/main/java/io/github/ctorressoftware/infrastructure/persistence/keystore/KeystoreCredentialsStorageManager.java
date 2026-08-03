package io.github.ctorressoftware.infrastructure.persistence.keystore;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import io.github.ctorressoftware.application.port.out.CredentialsStorageManager;
import io.github.ctorressoftware.infrastructure.persistence.exception.CredentialsStorageException;
import io.github.ctorressoftware.infrastructure.persistence.exception.InaccessibleCredentialsException;

public class KeystoreCredentialsStorageManager implements CredentialsStorageManager {

    private final KeyringFactory keyringFactory;
    private final String UNKNOWN_STORAGE_TYPE = "UNKNOWN";

    public KeystoreCredentialsStorageManager(KeyringFactory keyringFactory) {
        this.keyringFactory = keyringFactory;
    }

    @Override
    public void store(String domain, String account, String secret) {

        String storageType = UNKNOWN_STORAGE_TYPE;
        try (Keyring keyring = keyringFactory.create()) {
            storageType = keyring.getKeyringStorageType().name();
            keyring.setPassword(domain, account, secret);
        } catch (PasswordAccessException e) {
            throw new InaccessibleCredentialsException(storageType, e);
        } catch (BackendNotSupportedException e) {
            // TODO: Implement fallback logic by writing a file with restricted permissions (chmod 600)
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new CredentialsStorageException("An error occurred trying to store " + account + " credentials", e);
        }
    }

    @Override
    public void delete(String domain, String account) {

        String storageType = UNKNOWN_STORAGE_TYPE;
        try (Keyring keyring = keyringFactory.create()) {
            storageType = keyring.getKeyringStorageType().name();
            keyring.deletePassword(domain, account);
        } catch (BackendNotSupportedException e) {
            // TODO: Implement fallback logic by writing a file with restricted permissions (chmod 600)
            throw new RuntimeException(e);
        } catch (PasswordAccessException e) {
            throw new InaccessibleCredentialsException(storageType, e);
        } catch (Exception e) {
            throw new CredentialsStorageException("An error occurred trying to remove " + account + " credentials", e);
        }
    }

    @Override
    public String find(String domain, String account) {

        String storageType = UNKNOWN_STORAGE_TYPE;
        try (final Keyring keyring = keyringFactory.create()) {
            storageType = keyring.getKeyringStorageType().name();
            return keyring.getPassword(domain, account);
        } catch (BackendNotSupportedException e) {
            // TODO: Implement fallback logic by writing a file with restricted permissions (chmod 600)
            throw new RuntimeException(e);
        } catch (PasswordAccessException e) {
            throw new InaccessibleCredentialsException(storageType, e);
        } catch (Exception e) {
            throw new CredentialsStorageException("An error occurred trying to find " + account + " credentials", e);
        }
    }
}