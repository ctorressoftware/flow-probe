package io.github.ctorressoftware.infrastructure.persistence.keystore;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import io.github.ctorressoftware.application.port.out.CredentialsStorageManager;
import io.github.ctorressoftware.infrastructure.persistence.exception.CredentialsStorageException;
import io.github.ctorressoftware.infrastructure.persistence.exception.InaccessibleCredentialsException;

public class KeystoreCredentialsStorageManager implements CredentialsStorageManager {

    @Override
    public void store(String domain, String account, String secret) {
        try (Keyring keyring = Keyring.create()) {
            keyring.setPassword(domain, account, secret);
        } catch (BackendNotSupportedException e) {
            // TODO: Implement fallback logic by writing a file with restricted permissions (chmod 600)
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new CredentialsStorageException("An error occurred trying to store " + account + " credentials", e);
        }
    }

    @Override
    public void delete(String domain, String account) {
        try (Keyring keyring = Keyring.create()) {
            keyring.deletePassword(domain, account);
        } catch (BackendNotSupportedException e) {
            // TODO: Implement fallback logic by writing a file with restricted permissions (chmod 600)
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new CredentialsStorageException("An error occurred trying to remove " + account + " credentials", e);
        }
    }

    @Override
    public String find(String domain, String account) {
        try (final Keyring keyring = Keyring.create()) {
            try {
                return keyring.getPassword(domain, account);
            } catch (PasswordAccessException e) {
                throw new InaccessibleCredentialsException(keyring.getKeyringStorageType().name());
            }
        } catch (BackendNotSupportedException e) {
            // TODO: Implement fallback logic by writing a file with restricted permissions (chmod 600)
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new CredentialsStorageException("An error occurred trying to find " + account + " credentials", e);
        }
    }
}