package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.application.port.out.ProviderConfigRepository;
import io.github.ctorressoftware.domain.model.ImpedimentTicket;

import java.util.Map;

public class AzureDevOpsWorkItemTicketCreator {

    private final AzureDevOpsWorkItemClient azureDevOpsWorkItemClient;
    private final ProviderConfigRepository providerConfigRepository;

    public AzureDevOpsWorkItemTicketCreator(
            AzureDevOpsWorkItemClient azureDevOpsWorkItemClient,
            ProviderConfigRepository providerConfigRepository) {
        this.azureDevOpsWorkItemClient = azureDevOpsWorkItemClient;
        this.providerConfigRepository = providerConfigRepository;
    }

    public AzureDevOpsWorkItemResponse create(ImpedimentTicket ticket) {
        var configuration = getAzureConfiguration();
        var request = AzureDevOpsCreateWorkItemRequest.from(ticket);
        return azureDevOpsWorkItemClient.createWorkItem(request, configuration);
    }

    private AzureDevOpsConfiguration getAzureConfiguration() {

        Map<String, String> azureCredentials = providerConfigRepository.findByDomainAndAccount(
                AzureDevOpsConfiguration.AZURE_DOMAIN,
                AzureDevOpsConfiguration.AZURE_ACCOUNT
        );

        String azureOrganization = azureCredentials.get("organization");
        String azureProject = azureCredentials.get("project");
        String azureWorkItemType = azureCredentials.get("workItemType");
        String azurePat = azureCredentials.get("pat");

        return new AzureDevOpsConfiguration(
                azureWorkItemType,
                azureOrganization,
                azureProject,
                azurePat
        );
    }
}
