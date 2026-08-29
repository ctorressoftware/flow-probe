package io.github.ctorressoftware.infrastructure.provider.azure;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.ctorressoftware.application.port.in.provider.configure.ProviderStatus;
import io.github.ctorressoftware.application.port.out.ProviderConfigRepository;

@ExtendWith(MockitoExtension.class)
class AzureProviderConfiguratorTest {
    
    @Mock
    private ProviderConfigRepository providerConfigRepository;

    private AzureProviderConfigurator azureProviderConfigurator;

    @BeforeEach
    void init() {
        this.azureProviderConfigurator = new AzureProviderConfigurator(providerConfigRepository);
    }

    @Test
    void shouldConfigureCredentialsSuccessfully() {

        Map<String, String> credentials = Map.of(
                "organization", "azure",
                "project", "my-project",
                "workItemType", "Impediment",
                "pat", "1234567890"
        );

        azureProviderConfigurator.configure(credentials);

        Mockito.verify(providerConfigRepository).save(credentials);
    }

    @Test
    void shouldRemoveCredentialsSuccessfully() {

        azureProviderConfigurator.remove();

        Mockito.verify(providerConfigRepository).remove();
    }

    @Test
    void shouldCheckIfProviderConfigurationIsStored() {

        Mockito.when(providerConfigRepository.exists()).thenReturn(true);

        ProviderStatus status = azureProviderConfigurator.status();

        Assertions.assertEquals(
            ProviderStatus.CONFIGURED, 
            status
        );

        Mockito.verify(providerConfigRepository).exists();
    }

    @Test
    void shouldCheckIfProviderConfigurationIsNotStored() {

        Mockito.when(providerConfigRepository.exists()).thenReturn(false);

        ProviderStatus status = azureProviderConfigurator.status();

        Assertions.assertEquals(
            ProviderStatus.NOT_CONFIGURED, 
            status
        );

        Mockito.verify(providerConfigRepository).exists();
    }
}
