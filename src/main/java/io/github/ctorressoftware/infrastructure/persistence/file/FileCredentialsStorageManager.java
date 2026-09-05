package io.github.ctorressoftware.infrastructure.persistence.file;

import io.github.ctorressoftware.application.port.out.CredentialsStorageManager;

/*
    TODO: Implement file-backed credential storage before wiring this adapter.
    Credentials must not be persisted as plaintext.
 */
public class FileCredentialsStorageManager implements CredentialsStorageManager {

    @Override
    public String find(String domain, String account) {
        return "";
    }

    @Override
    public void store(String domain, String account, String secret) {

    }

    @Override
    public void delete(String domain, String account) {

    }
}
