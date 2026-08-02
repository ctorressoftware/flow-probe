package io.github.ctorressoftware.infrastructure.persistence.keystore;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import io.github.ctorressoftware.infrastructure.persistence.exception.CredentialsStorageException;
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

        RuntimeException cause = new RuntimeException("Unexpected failure");

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
    void shouldWrapUnexpectedExceptionWhenFindingCredentials()
            throws Exception {

        String domain = "flowprobe";
        String account = "azure";

        RuntimeException cause = new RuntimeException("Unexpected failure");

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

        storageManager.delete(domain, account);

        //noinspection resource
        Mockito.verify(keyringFactory).create();
        Mockito.verify(keyring).deletePassword(domain, account);
        Mockito.verify(keyring).close();
        Mockito.verifyNoMoreInteractions(keyringFactory, keyring);
    }

}
