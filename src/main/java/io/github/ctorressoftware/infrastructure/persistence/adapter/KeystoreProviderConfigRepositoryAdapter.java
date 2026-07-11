package io.github.ctorressoftware.infrastructure.persistence.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.CredentialsStorageManager;
import io.github.ctorressoftware.application.port.out.ProviderConfigRepository;

import java.util.Map;

public class KeystoreProviderConfigRepositoryAdapter implements ProviderConfigRepository {

    public CredentialsStorageManager credentialsStorageManager;

    public KeystoreProviderConfigRepositoryAdapter(CredentialsStorageManager credentialsStorageManager) {
        this.credentialsStorageManager = credentialsStorageManager;
    }

    @Override
    public void save(Map<String, String> credentials) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonCredentials = mapper.writeValueAsString(credentials);
            credentialsStorageManager.store("flowprobe", "azure", jsonCredentials); // return Credentials.CONFIGURED;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> findByDomainAndAccount(String domain, String account) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonSecret = credentialsStorageManager.find(domain, account);

        try {
            return mapper.readValue(jsonSecret, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException( // InvalidStoredCredentialsException
                    "Stored credentials contain invalid JSON for domain '%s' and account '%s'"
                            .formatted(domain, account),
                    e
            );
        }
    }

    @Override
    public void remove() {
        credentialsStorageManager.delete(
                "flowprobe",
                "azure"
        );
    }

    @Override
    public boolean exists() {
        String jsonSecret = credentialsStorageManager.find("flowprobe", "azure");
        return !jsonSecret.isBlank();
    }
}
