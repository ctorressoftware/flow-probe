package io.github.ctorressoftware.infrastructure.provider.azure;

import io.github.ctorressoftware.application.port.in.provider.configure.ProviderStatus;
import io.github.ctorressoftware.application.port.out.ProviderConfigurator;

import java.util.Map;

public class AzureProviderConfigurator implements ProviderConfigurator {

    @Override
    public void configure(Map<String, String> credentials) {

    }

    @Override
    public void remove() {

    }

    @Override
    public ProviderStatus status() {
        return null;
    }
}
