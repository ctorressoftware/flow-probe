package io.github.ctorressoftware.application.usecase.provider.configure;

import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderCommand;
import io.github.ctorressoftware.application.port.in.provider.configure.Provider;
import io.github.ctorressoftware.application.port.out.ProviderConfigRepository;
import io.github.ctorressoftware.application.port.out.ProviderConfigurator;
import io.github.ctorressoftware.application.port.out.ProviderPrompt;
import io.github.ctorressoftware.domain.exception.UnsupportedProviderException;
import io.github.ctorressoftware.infrastructure.provider.azure.AzureProviderConfigurator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ConfigureProviderHandlerTest {

    @Mock
    private ProviderConfigRepository providerConfigRepository;

    private ConfigureProviderHandler configureProviderHandler;

    @Test
    void shouldThrowUnsupportedProviderException() {

        Provider provider = Provider.AZURE;

        Map<Provider, ProviderConfigurator> configurators = Map.of(
                provider, new AzureProviderConfigurator(providerConfigRepository)
        );

        Map<Provider, ProviderPrompt> prompts = Map.of();

        configureProviderHandler = new ConfigureProviderHandler(configurators, prompts);

        UnsupportedProviderException exception = Assertions.assertThrows(
                UnsupportedProviderException.class,
                () -> configureProviderHandler.configure(new ConfigureProviderCommand(provider))
        );

        Assertions.assertEquals(
                "Unsupported provider: " + provider.name(),
                exception.getMessage()
        );
    }


}
