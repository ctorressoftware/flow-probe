package io.github.ctorressoftware.infrastructure.persistence.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.CredentialsStorageManager;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class KeystoreProviderConfigRepositoryAdapterTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CredentialsStorageManager credentialsStorageManager;

    private KeystoreProviderConfigRepositoryAdapter configurator;

    @BeforeEach
    void init() {
        this.configurator = new KeystoreProviderConfigRepositoryAdapter(objectMapper, credentialsStorageManager);
    }

    @Test
    void shouldSaveCredentialsWithoutExceptions()
            throws JsonProcessingException {

        Map<String, String> credentials = Map.of("username", "password");

        String serializedCredentials = "{\"username\":\"password\"}";

        Mockito
                .when(objectMapper.writeValueAsString(credentials))
                .thenReturn(serializedCredentials);

        Assertions.assertDoesNotThrow(
                () -> configurator.save(credentials)
        );

        Mockito.verify(credentialsStorageManager).store(
                AzureDevOpsConfiguration.AZURE_DOMAIN,
                AzureDevOpsConfiguration.AZURE_ACCOUNT,
                serializedCredentials
        );
    }
}
