package io.github.ctorressoftware.infrastructure.persistence.keystore;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.KeyringStorageType;
import com.github.javakeyring.PasswordAccessException;
import io.github.ctorressoftware.infrastructure.persistence.exception.CredentialsStorageException;
import io.github.ctorressoftware.infrastructure.persistence.exception.InaccessibleCredentialsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeystoreCredentialsStorageManagerTest {

    @Mock
    private KeyringFactory keyringFactory;

    @Mock
    private Keyring keyring;

    private KeystoreCredentialsStorageManager storageManager;

    @BeforeEach
    void init() throws BackendNotSupportedException {
        Mockito.when(keyringFactory.create()).thenReturn(keyring);
        storageManager = new KeystoreCredentialsStorageManager(keyringFactory);
    }

    @Test
    void shouldStoreCredentials() throws Exception {

        String domain = "flowprobe";
        String account = "azure";
        String credentials = "{\"username\":\"password\"}";

        KeyringStorageType storageType = KeyringStorageType.OSX_KEYCHAIN;

        Mockito
                .when(keyring.getKeyringStorageType())
                .thenReturn(storageType);

        storageManager.store(domain, account, credentials);

        //noinspection resource
        Mockito.verify(keyringFactory).create();
        Mockito.verify(keyring).setPassword(domain, account, credentials);
        Mockito.verify(keyring).close();
        Mockito.verifyNoMoreInteractions(keyringFactory, keyring);
    }

    @Test
    void shouldWrapUnexpectedExceptionsWhenStoringCredentials()
            throws Exception {

        String domain = "flowprobe";
        String account = "azure";
        String credentials = "{\"username\":\"password\"}";

        KeyringStorageType storageType = KeyringStorageType.OSX_KEYCHAIN;

        RuntimeException cause = new RuntimeException("Unexpected failure");

        Mockito
                .when(keyring.getKeyringStorageType())
                .thenReturn(storageType);

        Mockito
                .doThrow(cause)
                .when(keyring)
                .setPassword(domain, account, credentials);

        CredentialsStorageException exception = Assertions.assertThrows(
                CredentialsStorageException.class,
                () -> storageManager.store(domain, account, credentials)
        );

        Assertions.assertEquals(
                "An error occurred trying to store " + account + " credentials",
                exception.getMessage()
        );

        Assertions.assertSame(cause, exception.getCause());

        //noinspection resource
        Mockito.verify(keyringFactory).create();
        Mockito.verify(keyring).setPassword(domain, account, credentials);
        Mockito.verify(keyring).close();
        Mockito.verifyNoMoreInteractions(keyringFactory, keyring);
    }

    @Test
    void shouldFindCredentials() throws Exception {

        String domain = "flowprobe";
        String account = "azure";
        String expectedCredentials = "{\"username\":\"password\"}";

        KeyringStorageType storageType = KeyringStorageType.OSX_KEYCHAIN;

        Mockito
                .when(keyring.getKeyringStorageType())
                .thenReturn(storageType);

        Mockito
                .when(keyring.getPassword(domain, account))
                .thenReturn(expectedCredentials);

        String actualCredentials = storageManager.find(domain, account);

        Assertions.assertEquals(expectedCredentials, actualCredentials);

        //noinspection resource
        Mockito.verify(keyringFactory).create();
        Mockito.verify(keyring).getPassword(domain, account);
        Mockito.verify(keyring).close();
        Mockito.verifyNoMoreInteractions(keyringFactory, keyring);
    }

    @Test
    void shouldWrapPasswordAccessExceptionWhenFindingCredentials()
            throws Exception {

        String domain = "flowprobe";
        String account = "azure";

        KeyringStorageType storageType = KeyringStorageType.OSX_KEYCHAIN;

        PasswordAccessException cause =
                new PasswordAccessException("PasswordAccessException");

        Mockito
                .when(keyring.getKeyringStorageType())
                .thenReturn(storageType);

        Mockito
                .when(keyring.getPassword(domain, account))
                .thenThrow(cause);

        InaccessibleCredentialsException exception = Assertions.assertThrows(
                InaccessibleCredentialsException.class,
                () -> storageManager.find(domain, account)
        );

        Assertions.assertEquals(
                "Credentials are inaccessible for the storage type: " + storageType.name(),
                exception.getMessage()
        );

        Assertions.assertSame(cause, exception.getCause());

        //noinspection resource
        Mockito.verify(keyringFactory).create();
        Mockito.verify(keyring).getKeyringStorageType();
        Mockito.verify(keyring).getPassword(domain, account);
        Mockito.verify(keyring).close();
        Mockito.verifyNoMoreInteractions(keyringFactory, keyring);
    }

    @Test
    void shouldWrapUnexpectedExceptionWhenFindingCredentials()
            throws Exception {

        String domain = "flowprobe";
        String account = "azure";

        KeyringStorageType storageType = KeyringStorageType.OSX_KEYCHAIN;

        RuntimeException cause = new RuntimeException("Unexpected failure");

        Mockito
                .when(keyring.getKeyringStorageType())
                .thenReturn(storageType);

        Mockito
                .when(keyring.getPassword(domain, account))
                .thenThrow(cause);

        CredentialsStorageException exception = Assertions.assertThrows(
                CredentialsStorageException.class,
                () -> storageManager.find(domain, account)
        );

        Assertions.assertEquals(
                "An error occurred trying to find " + account + " credentials",
                exception.getMessage()
        );

        Assertions.assertSame(cause, exception.getCause());

        //noinspection resource
        Mockito.verify(keyringFactory).create();
        Mockito.verify(keyring).getPassword(domain, account);
        Mockito.verify(keyring).close();
        Mockito.verifyNoMoreInteractions(keyringFactory, keyring);
    }

    @Test
    void shouldRemoveCredentials() throws Exception {

        String domain = "flowprobe";
        String account = "azure";

        KeyringStorageType storageType = KeyringStorageType.OSX_KEYCHAIN;

        Mockito
                .when(keyring.getKeyringStorageType())
                .thenReturn(storageType);

        storageManager.delete(domain, account);

        //noinspection resource
        Mockito.verify(keyringFactory).create();
        Mockito.verify(keyring).deletePassword(domain, account);
        Mockito.verify(keyring).close();
        Mockito.verifyNoMoreInteractions(keyringFactory, keyring);
    }

    @Test
    void shouldWrapUnexpectedExceptionWhenRemovingCredentials() throws Exception {

        String domain = "flowprobe";
        String account = "azure";

        KeyringStorageType storageType = KeyringStorageType.OSX_KEYCHAIN;

        RuntimeException cause = new RuntimeException("Unexpected failure");

        Mockito
                .when(keyring.getKeyringStorageType())
                .thenReturn(storageType);

        Mockito
                .doThrow(cause)
                .when(keyring)
                .deletePassword(domain, account);

        CredentialsStorageException exception = Assertions.assertThrows(
                CredentialsStorageException.class,
                () -> storageManager.delete(domain, account)
        );

        Assertions.assertEquals(
                "An error occurred trying to remove " + account + " credentials",
                exception.getMessage()
        );

        Assertions.assertSame(cause, exception.getCause());

        //noinspection resource
        Mockito.verify(keyringFactory).create();
        Mockito.verify(keyring).deletePassword(domain, account);
        Mockito.verify(keyring).close();
        Mockito.verifyNoMoreInteractions(keyringFactory, keyring);
    }
}
