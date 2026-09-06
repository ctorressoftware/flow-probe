package io.github.ctorressoftware.application.port.out;

public interface ProviderConfigRepository {
    void save(ProviderConfig credentials);
    <T extends ProviderConfig> T findByDomainAndAccount(String domain, String account, Class<T> configType);
    void remove();
    boolean exists();
}
