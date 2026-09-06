package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.application.port.out.ProviderConfigRepository;
import io.github.ctorressoftware.domain.model.ImpedimentTicket;

public class AzureDevOpsWorkItemTicketCreator {

    private final static String AZURE_DOMAIN = "flowprobe";
    private final static String AZURE_ACCOUNT = "azure";
    private final AzureDevOpsWorkItemClient azureDevOpsWorkItemClient;
    private final ProviderConfigRepository providerConfigRepository;

    public AzureDevOpsWorkItemTicketCreator(
            AzureDevOpsWorkItemClient azureDevOpsWorkItemClient,
            ProviderConfigRepository providerConfigRepository) {
        this.azureDevOpsWorkItemClient = azureDevOpsWorkItemClient;
        this.providerConfigRepository = providerConfigRepository;
    }

    public AzureDevOpsWorkItemResponse create(ImpedimentTicket ticket) {
        var configuration = getAzureDevOpsConfiguration();
        var request = AzureDevOpsCreateWorkItemRequest.from(ticket);
        return azureDevOpsWorkItemClient.createWorkItem(request, configuration);
    }

    private AzureDevOpsConfig getAzureDevOpsConfiguration() {

        return providerConfigRepository.findByDomainAndAccount(
                AZURE_DOMAIN,
                AZURE_ACCOUNT,
                AzureDevOpsConfig.class
        );
    }
}
