package io.github.ctorressoftware.application.port.out;

import java.util.Map;

public interface ProviderConfigRepository { // TODO: ProviderCredentialsRepository could be better
    void save(Map<String, String> credentials);
    Map<String, String> findByDomainAndAccount(String domain, String account);
    void remove();
    boolean exists();
}
