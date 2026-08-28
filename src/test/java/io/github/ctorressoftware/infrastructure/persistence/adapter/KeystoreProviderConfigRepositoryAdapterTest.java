package io.github.ctorressoftware.infrastructure.persistence.adapter;

import io.github.ctorressoftware.application.port.out.CredentialsStorageManager;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.infrastructure.json.jackson.JacksonJsonProcessor;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class KeystoreProviderConfigRepositoryAdapterTest {

    @Mock
    private CredentialsStorageManager credentialsStorageManager;

    private ObjectMapper objectMapper;

    private JsonProcessor jsonProcessor;

    private KeystoreProviderConfigRepositoryAdapter configurator;

    @BeforeEach
    void init() {
        this.objectMapper = new ObjectMapper();
        this.jsonProcessor = new JacksonJsonProcessor(objectMapper);
        this.configurator = new KeystoreProviderConfigRepositoryAdapter(
            jsonProcessor, 
            credentialsStorageManager
        );
    }

    @Test
    void shouldSaveCredentialsSuccessfully() {

        Map<String, String> credentials = Map.of("username", "password");

        String serializedCredentials = "{\"username\":\"password\"}";

        Assertions.assertDoesNotThrow(
                () -> configurator.save(credentials)
        );

        Mockito.verify(credentialsStorageManager).store(
                AzureDevOpsConfiguration.AZURE_DOMAIN,
                AzureDevOpsConfiguration.AZURE_ACCOUNT,
                serializedCredentials
        );
    }

    @Test
    void shouldFindCredentialsByDomainAndAccountSuccessfully() {

        String domain = "flowprobe";
        String account = "azure";

        Map<String, String> expected = Map.of("username", "password");

        Mockito
            .when(credentialsStorageManager.find(domain, account))
            .thenReturn("{\"username\":\"password\"}");

        Map<String, String> credentials = configurator.findByDomainAndAccount(domain, account);

        Assertions.assertEquals(expected, credentials);
        Mockito.verify(credentialsStorageManager).find(domain, account);
        Mockito.verifyNoMoreInteractions(credentialsStorageManager);
    }

    @Test
    void shouldRemoveStoredCredentialsSuccessfully() {

        String domain = "flowprobe";
        String account = "azure";

        Assertions
            .assertDoesNotThrow(() -> configurator.remove());

        Mockito.verify(credentialsStorageManager).delete(domain, account);
        Mockito.verifyNoMoreInteractions(credentialsStorageManager);
    }


    @Test
    void shouldReturnTrueIfProviderCredentialsAreStored() {

        String domain = "flowprobe";
        String account = "azure";
        String serializedCredentials = "{\"username\":\"password\"}";

        Mockito
                .when(credentialsStorageManager.find(domain, account))
                .thenReturn(serializedCredentials);

        Assertions.assertTrue(configurator.exists());

        Mockito.verify(credentialsStorageManager).find(domain, account);
        Mockito.verifyNoMoreInteractions(credentialsStorageManager);
    }

    @Test
    void shouldReturnFalseIfProviderCredentialsAreNotStored() {

        String domain = "flowprobe";
        String account = "azure";

        Mockito
                .when(credentialsStorageManager.find(domain, account))
                .thenReturn("");

        Assertions.assertFalse(configurator.exists());

        Mockito.verify(credentialsStorageManager).find(domain, account);
        Mockito.verifyNoMoreInteractions(credentialsStorageManager);
    }
}
