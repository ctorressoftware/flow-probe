package io.github.ctorressoftware.application.usecase.provider.configure;

import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderCommand;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderResult;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderUseCase;
import io.github.ctorressoftware.application.port.out.ProviderConfigurator;
import io.github.ctorressoftware.application.port.out.ProviderPrompt;
import io.github.ctorressoftware.infrastructure.provider.azure.ProviderConfiguratorFactory;
import io.github.ctorressoftware.infrastructure.provider.azure.ProviderPromptFactory;

import java.util.Map;

public class ConfigureProviderHandler implements ConfigureProviderUseCase {

    private final ProviderConfiguratorFactory configuratorFactory;
    private final ProviderPromptFactory promptFactory;

    public ConfigureProviderHandler(
            ProviderConfiguratorFactory configuratorFactory,
            ProviderPromptFactory promptFactory) {
        this.configuratorFactory = configuratorFactory;
        this.promptFactory = promptFactory;
    }

    @Override
    public ConfigureProviderResult configure(ConfigureProviderCommand command) {

        ProviderPrompt providerPrompt = promptFactory.getPromptByProvider(command.provider());
        ProviderConfigurator configurator = configuratorFactory.getConfiguratorProvider(command.provider());

        final Map<String, String> credentials = providerPrompt.prompt();
        configurator.configure(credentials);

        return new ConfigureProviderResult(true);
    }
}
