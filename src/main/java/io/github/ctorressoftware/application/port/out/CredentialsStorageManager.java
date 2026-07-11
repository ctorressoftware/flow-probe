package io.github.ctorressoftware.application.port.out;

public interface CredentialsStorageManager {
    String find(String domain, String account);
    void store(String domain, String account, String secret);
    void delete(String domain, String account);
}
