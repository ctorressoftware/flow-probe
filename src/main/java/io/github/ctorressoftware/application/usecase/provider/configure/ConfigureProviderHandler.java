package io.github.ctorressoftware.application.usecase.provider.configure;

import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderCommand;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderResult;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderUseCase;
import io.github.ctorressoftware.application.port.in.provider.configure.Provider;
import io.github.ctorressoftware.application.port.out.ProviderConfigurator;
import io.github.ctorressoftware.application.port.out.ProviderPrompt;
import io.github.ctorressoftware.domain.exception.UnsupportedProviderException;

import java.util.Map;

public class ConfigureProviderHandler implements ConfigureProviderUseCase {

    private final Map<Provider, ProviderConfigurator> configurators;
    private final Map<Provider, ProviderPrompt> prompts;

    public ConfigureProviderHandler(
            Map<Provider, ProviderConfigurator> configurators,
            Map<Provider, ProviderPrompt> prompts
    ) {
        this.configurators = Map.copyOf(configurators);
        this.prompts = Map.copyOf(prompts);
    }

    @Override
    public ConfigureProviderResult configure(ConfigureProviderCommand command) {
        Provider provider = command.provider();

        ProviderPrompt prompt = getRequired(prompts, provider);
        ProviderConfigurator configurator = getRequired(configurators, provider);

        Map<String, String> credentials = prompt.prompt();
        configurator.configure(credentials);

        return new ConfigureProviderResult(true);
    }

    private static <T> T getRequired(Map<Provider, T> implementations, Provider provider) {
        T implementation = implementations.get(provider);

        if (implementation == null) {
            throw new UnsupportedProviderException(provider);
        }

        return implementation;
    }
}
