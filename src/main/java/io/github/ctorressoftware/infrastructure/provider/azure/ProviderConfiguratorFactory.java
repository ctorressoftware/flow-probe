package io.github.ctorressoftware.infrastructure.provider.azure;

import io.github.ctorressoftware.application.port.out.ProviderConfigurator;

import java.util.Map;

public class ProviderConfiguratorFactory {

    String DEFAULT_PROVIDER = "AZURE";
    private final Map<String, ProviderConfigurator> providerConfigurators;

    public ProviderConfiguratorFactory(Map<String, ProviderConfigurator> providerConfigurators) {
        this.providerConfigurators = providerConfigurators;
    }

    public ProviderConfigurator getConfiguratorProvider(String provider) {
        return providerConfigurators.getOrDefault(
                provider,
                providerConfigurators.get(DEFAULT_PROVIDER)
        );
    }
}
