package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.application.port.out.ProviderConfigRepository;
import io.github.ctorressoftware.domain.model.ImpedimentTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class AzureDevOpsWorkItemTicketCreatorTest {

    @Mock
    private AzureDevOpsWorkItemClient azureDevOpsWorkItemClient;

    @Mock
    private ProviderConfigRepository providerConfigRepository;

    @Test
    void shouldCreateAzureDevOpsWorkItemFromImpedimentTicket() {

        ImpedimentTicket ticket =
                ImpedimentTicket.create("Title", "Description");

        AzureDevOpsConfig config = new AzureDevOpsConfig(
                "azure",
                "my-project",
                "impediment",
                "1234567890"
        );

        AzureDevOpsWorkItemResponse expectedResponse =
                Mockito.mock(AzureDevOpsWorkItemResponse.class);

        Mockito
                .when(providerConfigRepository.findByDomainAndAccount(
                        "flowprobe",
                        "azure",
                        AzureDevOpsConfig.class
                ))
                .thenReturn(config);

        Mockito
                .when(azureDevOpsWorkItemClient.createWorkItem(
                        Mockito.any(AzureDevOpsCreateWorkItemRequest.class),
                        Mockito.any(AzureDevOpsConfig.class)
                ))
                .thenReturn(expectedResponse);

        AzureDevOpsWorkItemTicketCreator creator =
                new AzureDevOpsWorkItemTicketCreator(
                        azureDevOpsWorkItemClient,
                        providerConfigRepository
                );

        AzureDevOpsWorkItemResponse actualResponse =
                creator.create(ticket);

        Assertions.assertSame(expectedResponse, actualResponse);

        Mockito.verify(providerConfigRepository)
                .findByDomainAndAccount("flowprobe", "azure", AzureDevOpsConfig.class);

        ArgumentCaptor<AzureDevOpsCreateWorkItemRequest> requestCaptor =
                ArgumentCaptor.forClass(AzureDevOpsCreateWorkItemRequest.class);

        ArgumentCaptor<AzureDevOpsConfig> configurationCaptor =
                ArgumentCaptor.forClass(AzureDevOpsConfig.class);

        Mockito.verify(azureDevOpsWorkItemClient)
                .createWorkItem(requestCaptor.capture(), configurationCaptor.capture());

        AzureDevOpsConfig actualConfiguration = configurationCaptor.getValue();

        Assertions.assertEquals("azure", actualConfiguration.organization());
        Assertions.assertEquals("my-project", actualConfiguration.project());
        Assertions.assertEquals("impediment", actualConfiguration.workItemType());
        Assertions.assertEquals("1234567890", actualConfiguration.pat());

        AzureDevOpsCreateWorkItemRequest actualRequest = requestCaptor.getValue();
        Assertions.assertNotNull(actualRequest);
        Mockito.verifyNoMoreInteractions(
                providerConfigRepository,
                azureDevOpsWorkItemClient
        );
    }
}