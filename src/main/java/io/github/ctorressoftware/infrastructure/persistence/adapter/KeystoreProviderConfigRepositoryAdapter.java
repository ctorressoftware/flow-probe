package io.github.ctorressoftware.infrastructure.persistence.adapter;

import io.github.ctorressoftware.application.exception.JsonDeserializationException;
import io.github.ctorressoftware.application.exception.JsonSerializationException;
import io.github.ctorressoftware.application.port.out.CredentialsStorageManager;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.application.port.out.ProviderConfig;
import io.github.ctorressoftware.application.port.out.ProviderConfigRepository;
import io.github.ctorressoftware.infrastructure.persistence.exception.CredentialsSavingException;
import io.github.ctorressoftware.infrastructure.persistence.exception.InvalidStoredCredentialsException;

public class KeystoreProviderConfigRepositoryAdapter implements ProviderConfigRepository {

    private final static String AZURE_DOMAIN = "flowprobe";
    private final static String AZURE_ACCOUNT = "azure";
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
    public void save(ProviderConfig credentials) {
        try {
            String jsonCredentials = jsonProcessor.serialize(credentials);
            credentialsStorageManager.store(AZURE_DOMAIN, AZURE_ACCOUNT, jsonCredentials);
        } catch (JsonSerializationException e) {
            throw new CredentialsSavingException("Could not prepare credentials for storage", e);
        }
    }

    @Override
    public <T extends ProviderConfig> T findByDomainAndAccount(
            String domain,
            String account,
            Class<T> configType
    ) {
        String jsonSecret = credentialsStorageManager.find(domain, account);

        try {
            return jsonProcessor.deserialize(jsonSecret, configType);
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
        credentialsStorageManager.delete(AZURE_DOMAIN, AZURE_ACCOUNT);
    }

    @Override
    public boolean exists() {
        String jsonSecret = credentialsStorageManager.find(AZURE_DOMAIN, AZURE_ACCOUNT);
        return !jsonSecret.isBlank();
    }
}
