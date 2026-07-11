package io.github.ctorressoftware.infrastructure.persistence.keystore;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import io.github.ctorressoftware.application.port.out.CredentialsStorageManager;

public class KeystoreCredentialsStorageManager implements CredentialsStorageManager {

    @Override
    public void store(String domain, String account, String secret) {
        try (Keyring keyring = Keyring.create()) {
            keyring.setPassword(domain, account, secret);
        } catch (BackendNotSupportedException e) { // TODO: Implement fallback logic by writing a file with restricted permissions (chmod 600)
            throw new RuntimeException(e);
        } catch (Exception e) { // TODO: create a custom exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String domain, String account) {
        try (Keyring keyring = Keyring.create()) {
            keyring.deletePassword(domain, account);
        } catch (BackendNotSupportedException e) { // TODO: Implement fallback logic by writing a file with restricted permissions (chmod 600)
            throw new RuntimeException(e);
        } catch (Exception e) { // TODO: create a custom exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public String find(String domain, String account) {
        try (final Keyring keyring = Keyring.create()) {
            try {
                return keyring.getPassword(domain, account);
            } catch (PasswordAccessException ex) {
                keyring.setPassword(domain, account, "ChangeMe");
                throw new RuntimeException("Please add the correct credentials to you keystore "
                        + keyring.getKeyringStorageType()
                        + ". The credential is stored under '" + domain + "|" + account + "'"
                        + "with a password that is currently 'ChangeMe'");
            }
        } catch (BackendNotSupportedException e) { // TODO: Implement fallback logic by writing a file with restricted permissions (chmod 600)
            throw new RuntimeException(e);
        } catch (Exception e) { // TODO: create a custom exception
            throw new RuntimeException(e);
        }
    }
}