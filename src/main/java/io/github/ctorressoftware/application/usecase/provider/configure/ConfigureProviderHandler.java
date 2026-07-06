package io.github.ctorressoftware.application.usecase.provider.configure;

import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderCommand;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderResult;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderUseCase;
import io.github.ctorressoftware.application.port.out.ProviderConfigurator;

public class ConfigureProviderHandler implements ConfigureProviderUseCase {

    private final ProviderConfigurator providerConfigurator;

    public ConfigureProviderHandler(ProviderConfigurator providerConfigurator) {
        this.providerConfigurator = providerConfigurator;
    }

    @Override
    public ConfigureProviderResult configure(ConfigureProviderCommand command) {
        providerConfigurator.configure();
        return new ConfigureProviderResult(true);
    }
}
