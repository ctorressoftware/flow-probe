package io.github.ctorressoftware.infrastructure.persistence.keystore;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
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
        String expectedCredentials = "{\"username\":\"password\"}";

        storageManager.store(domain, account, expectedCredentials);

        Mockito.verify(keyringFactory).create();
        Mockito.verify(keyring).setPassword(domain, account, expectedCredentials);
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

        Mockito.verify(keyringFactory).create();
        Mockito.verify(keyring).deletePassword(domain, account);
        Mockito.verify(keyring).close();
        Mockito.verifyNoMoreInteractions(keyringFactory, keyring);
    }

}
