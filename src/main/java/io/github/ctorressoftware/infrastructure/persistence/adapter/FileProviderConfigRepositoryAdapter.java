package io.github.ctorressoftware.infrastructure.persistence.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.CredentialsStorageManager;
import io.github.ctorressoftware.application.port.out.ProviderConfigRepository;
import java.util.Map;

public class FileProviderConfigRepositoryAdapter implements ProviderConfigRepository {

    private final ObjectMapper objectMapper;
    private final CredentialsStorageManager credentialsStorageManager;

    public FileProviderConfigRepositoryAdapter(
            ObjectMapper objectMapper,
            CredentialsStorageManager credentialsStorageManager
    ) {
        this.objectMapper = objectMapper;
        this.credentialsStorageManager = credentialsStorageManager;
    }

    @Override
    public void save(Map<String, String> credentials) {
        // TODO: complete this method
    }

    @Override
    public Map<String, String> findByDomainAndAccount(String domain, String account) {
        // TODO: complete this method
        return null;
    }

    @Override
    public void remove() {
        // TODO: complete this method
    }

    @Override
    public boolean exists() {
        // TODO: complete this method
        return false;
    }
}
