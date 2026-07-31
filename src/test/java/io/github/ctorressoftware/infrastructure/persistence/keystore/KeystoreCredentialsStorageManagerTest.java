package io.github.ctorressoftware.infrastructure.persistence.keystore;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KeystoreCredentialsStorageManagerTest {

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
        storageManager.store("flowprobe", "azure", "secret");

        try {
            Mockito.verify(keyring)
                    .setPassword("flowprobe", "azure", "secret");
        } catch (PasswordAccessException e) {
            throw new RuntimeException(e);
        }

        Mockito.verify(keyring).close();
    }

    @Test
    void shouldFindCredentials() throws Exception {

        Mockito
                .when(keyring.getPassword("flowprobe", "azure"))
                .thenReturn("{\"username\":\"password\"}");

        String serializedCredentials = storageManager.find("flowprobe", "azure");

        try {
            String mockCredentials = Mockito.verify(keyring)
                    .getPassword("flowprobe", "azure");

            Assertions.assertEquals(mockCredentials, serializedCredentials);

        } catch (PasswordAccessException e) {
            throw new RuntimeException(e);
        }

        Mockito.verify(keyring).close();
    }

}
