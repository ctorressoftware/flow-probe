package io.github.ctorressoftware.infrastructure.persistence.adapter;

import io.github.ctorressoftware.application.exception.JsonSerializationException;
import io.github.ctorressoftware.application.port.out.CredentialsStorageManager;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.infrastructure.json.jackson.JacksonJsonProcessor;
import io.github.ctorressoftware.infrastructure.persistence.exception.CredentialsSavingException;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class KeystoreProviderConfigRepositoryAdapterTest {

    private final static String AZURE_DOMAIN = "flowprobe";
    private final static String AZURE_ACCOUNT = "azure";

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
                credentialsStorageManager);
    }

    @Test
    void shouldSaveCredentialsSuccessfully() {

        AzureDevOpsConfig config = new AzureDevOpsConfig(
                "azure",
                "my-project",
                "impediment",
                "1234567890"
        );

        String serialized =
                "{\"organization\":\"azure\",\"project\":\"my-project\",\"workItemType\":\"impediment\",\"pat\":\"1234567890\"}";

        Assertions.assertDoesNotThrow(
                () -> configurator.save(config));

        Mockito.verify(credentialsStorageManager).store(AZURE_DOMAIN, AZURE_ACCOUNT, serialized);
    }

    @Test
    void shouldFindCredentialsByDomainAndAccountSuccessfully() {

        AzureDevOpsConfig expected = new AzureDevOpsConfig(
                "azure",
                "my-project",
                "impediment",
                "1234567890"
        );

        Mockito
                .when(credentialsStorageManager.find(AZURE_DOMAIN, AZURE_ACCOUNT))
                .thenReturn("""
                    {
                      "organization": "azure",
                      "project": "my-project",
                      "workItemType": "impediment",
                      "pat": "1234567890"
                    }
                    """);

        AzureDevOpsConfig azureConfig = configurator
                .findByDomainAndAccount(AZURE_DOMAIN, AZURE_ACCOUNT, AzureDevOpsConfig.class);

        Assertions.assertEquals(expected, azureConfig);
        Mockito.verify(credentialsStorageManager).find(AZURE_DOMAIN, AZURE_ACCOUNT);
        Mockito.verifyNoMoreInteractions(credentialsStorageManager);
    }

    @Test
    void shouldRemoveStoredCredentialsSuccessfully() {

        Assertions.assertDoesNotThrow(() -> configurator.remove());
        Mockito.verify(credentialsStorageManager).delete(AZURE_DOMAIN, AZURE_ACCOUNT);
        Mockito.verifyNoMoreInteractions(credentialsStorageManager);
    }

    @Test
    void shouldReturnTrueIfProviderCredentialsAreStored() {

        String serialized = """
                    {
                      "organization": "azure",
                      "project": "my-project",
                      "workItemType": "impediment",
                      "pat": "1234567890"
                    }
                    """;

        Mockito
                .when(credentialsStorageManager.find(AZURE_DOMAIN, AZURE_ACCOUNT))
                .thenReturn(serialized);

        Assertions.assertTrue(configurator.exists());

        Mockito.verify(credentialsStorageManager).find(AZURE_DOMAIN, AZURE_ACCOUNT);
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

    @Test
    void shouldWrapJsonSerializationExceptionAsCredentialsSavingException() {

        JacksonJsonProcessor jacksonJsonProcessor = Mockito.mock(JacksonJsonProcessor.class);
        
        configurator = new KeystoreProviderConfigRepositoryAdapter(
                jacksonJsonProcessor,
                credentialsStorageManager
        );

        JsonSerializationException cause =
                Mockito.mock(JsonSerializationException.class);

        AzureDevOpsConfig config = new AzureDevOpsConfig(
                "azure",
                "my-project",
                "impediment",
                "1234567890"
        );

        Mockito
            .when(jacksonJsonProcessor.serialize(config))
            .thenThrow(cause);

        CredentialsSavingException exception = Assertions.assertThrows(
            CredentialsSavingException.class,
            () -> configurator.save(config)
        );

        Assertions.assertEquals(
            "Could not prepare credentials for storage",
            exception.getMessage()
        );

        assertSame(cause, exception.getCause());
    }
}
