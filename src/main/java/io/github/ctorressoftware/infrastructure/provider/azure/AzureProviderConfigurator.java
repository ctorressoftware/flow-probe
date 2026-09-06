package io.github.ctorressoftware.infrastructure.provider.azure;

import io.github.ctorressoftware.application.port.in.provider.configure.ProviderStatus;
import io.github.ctorressoftware.application.port.out.ProviderConfig;
import io.github.ctorressoftware.application.port.out.ProviderConfigRepository;
import io.github.ctorressoftware.application.port.out.ProviderConfigurator;

public class AzureProviderConfigurator implements ProviderConfigurator {

    private final ProviderConfigRepository providerConfigRepository;

    public AzureProviderConfigurator(ProviderConfigRepository providerConfigRepository) {
        this.providerConfigRepository = providerConfigRepository;
    }

    @Override
    public void configure(ProviderConfig config) {
        providerConfigRepository.save(config);
    }

    @Override
    public void remove() {
        providerConfigRepository.remove();
    }

    @Override
    public ProviderStatus status() {
        return providerConfigRepository.exists() ?
                ProviderStatus.CONFIGURED :
                ProviderStatus.NOT_CONFIGURED;
    }
}
