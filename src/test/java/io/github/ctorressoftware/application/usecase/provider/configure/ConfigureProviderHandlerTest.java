package io.github.ctorressoftware.application.usecase.provider.configure;

import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderCommand;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderResult;
import io.github.ctorressoftware.application.port.in.provider.configure.Provider;
import io.github.ctorressoftware.application.port.out.ProviderConfigRepository;
import io.github.ctorressoftware.application.port.out.ProviderConfigurator;
import io.github.ctorressoftware.application.port.out.ProviderPrompt;
import io.github.ctorressoftware.domain.exception.UnsupportedProviderException;
import io.github.ctorressoftware.infrastructure.provider.azure.AzureProviderConfigurator;
import io.github.ctorressoftware.infrastructure.provider.azure.AzureProviderPrompt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class ConfigureProviderHandlerTest {

    @Mock
    private ProviderConfigRepository providerConfigRepository;

    private ConfigureProviderHandler configureProviderHandler;

    @Test
    void shouldConfigureProviderProperly() {

        Provider provider = Provider.AZURE;
        AzureProviderPrompt mockAzureProviderPrompt = Mockito.mock(AzureProviderPrompt.class);
        AzureProviderConfigurator mockAzureProviderConfigurator = Mockito.mock(AzureProviderConfigurator.class);

        Map<Provider, ProviderConfigurator> configurators = Map.of(provider, mockAzureProviderConfigurator);
        Map<Provider, ProviderPrompt> prompts = Map.of(provider, mockAzureProviderPrompt);

        configureProviderHandler = new ConfigureProviderHandler(configurators, prompts);

        Map<String, String> credentials = Map.of(
                "organization", "organization",
                "project", "project",
                "workItemType", "itemType",
                "pat", "1234567890987654321"
        );

        Mockito
                .when(mockAzureProviderPrompt.prompt())
                .thenReturn(credentials);

        Mockito
                .doNothing()
                .when(mockAzureProviderConfigurator)
                .configure(credentials);

        ConfigureProviderResult result = configureProviderHandler
                .configure(new ConfigureProviderCommand(provider));

        Assertions.assertTrue(result.configured());
    }

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
