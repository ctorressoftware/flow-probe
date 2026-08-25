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

        Map<String, String> credentials = Map.of(
                "organization", "Organization",
                "project", "Project",
                "workItemType", "Impediment",
                "pat", "123456789"
        );

        AzureDevOpsWorkItemResponse expectedResponse =
                Mockito.mock(AzureDevOpsWorkItemResponse.class);

        Mockito
                .when(providerConfigRepository.findByDomainAndAccount(
                        AzureDevOpsConfiguration.AZURE_DOMAIN,
                        AzureDevOpsConfiguration.AZURE_ACCOUNT
                ))
                .thenReturn(credentials);

        Mockito
                .when(azureDevOpsWorkItemClient.createWorkItem(
                        Mockito.any(AzureDevOpsCreateWorkItemRequest.class),
                        Mockito.any(AzureDevOpsConfiguration.class)
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
                .findByDomainAndAccount(
                        AzureDevOpsConfiguration.AZURE_DOMAIN,
                        AzureDevOpsConfiguration.AZURE_ACCOUNT
                );

        ArgumentCaptor<AzureDevOpsCreateWorkItemRequest> requestCaptor =
                ArgumentCaptor.forClass(AzureDevOpsCreateWorkItemRequest.class);

        ArgumentCaptor<AzureDevOpsConfiguration> configurationCaptor =
                ArgumentCaptor.forClass(AzureDevOpsConfiguration.class);

        Mockito.verify(azureDevOpsWorkItemClient)
                .createWorkItem(
                        requestCaptor.capture(),
                        configurationCaptor.capture()
                );

        AzureDevOpsConfiguration actualConfiguration =
                configurationCaptor.getValue();

        Assertions.assertEquals(
                "Impediment",
                actualConfiguration.azureWorkItemType()
        );

        Assertions.assertEquals(
                "Organization",
                actualConfiguration.azureOrganization()
        );

        Assertions.assertEquals(
                "Project",
                actualConfiguration.azureProject()
        );

        Assertions.assertEquals(
                "123456789",
                actualConfiguration.azurePat()
        );

        AzureDevOpsCreateWorkItemRequest actualRequest =
                requestCaptor.getValue();

        Assertions.assertNotNull(actualRequest);

        Mockito.verifyNoMoreInteractions(
                providerConfigRepository,
                azureDevOpsWorkItemClient
        );
    }
}