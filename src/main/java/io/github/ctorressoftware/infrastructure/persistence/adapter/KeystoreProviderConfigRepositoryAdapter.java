package io.github.ctorressoftware.infrastructure.persistence.adapter;

import io.github.ctorressoftware.application.exception.JsonDeserializationException;
import io.github.ctorressoftware.application.exception.JsonSerializationException;
import io.github.ctorressoftware.application.port.out.CredentialsStorageManager;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.application.port.out.ProviderConfigRepository;
import io.github.ctorressoftware.infrastructure.persistence.exception.CredentialsSavingException;
import io.github.ctorressoftware.infrastructure.persistence.exception.InvalidStoredCredentialsException;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsConfiguration;

import java.util.Map;

public class KeystoreProviderConfigRepositoryAdapter implements ProviderConfigRepository {

    private final JsonProcessor jsonProcessor;
    private final CredentialsStorageManager credentialsStorageManager;

    public KeystoreProviderConfigRepositoryAdapter(
            JsonProcessor jsonProcessor,
            CredentialsStorageManager credentialsStorageManager
    ) {
        this.jsonProcessor = jsonProcessor;
        this.credentialsStorageManager = credentialsStorageManager;
    }

    @Override
    public void save(Map<String, String> credentials) {
        try {
            String jsonCredentials = jsonProcessor.serialize(credentials);
            credentialsStorageManager.store(
                    AzureDevOpsConfiguration.AZURE_DOMAIN,
                    AzureDevOpsConfiguration.AZURE_ACCOUNT,
                    jsonCredentials
            ); // TODO: return Credentials.CONFIGURED or a boolean to validate;
        } catch (JsonSerializationException e) {
            throw new CredentialsSavingException("Could not prepare credentials for storage", e);
        }
    }

    @Override
    public Map<String, String> findByDomainAndAccount(String domain, String account) {
        String jsonSecret = credentialsStorageManager.find(domain, account);

        try {
            return jsonProcessor.readStringMap(jsonSecret);
        } catch (JsonDeserializationException e) {
            throw new InvalidStoredCredentialsException(
                    "Stored credentials contain invalid JSON for domain '%s' and account '%s'"
                            .formatted(domain, account),
                    e
            );
        }
    }

    @Override
    public void remove() {
        credentialsStorageManager.delete(
                AzureDevOpsConfiguration.AZURE_DOMAIN,
                AzureDevOpsConfiguration.AZURE_ACCOUNT
        );
    }

    @Override
    public boolean exists() {
        String jsonSecret = credentialsStorageManager.find(
                AzureDevOpsConfiguration.AZURE_DOMAIN,
                AzureDevOpsConfiguration.AZURE_ACCOUNT
        );
        return !jsonSecret.isBlank();
    }
}
