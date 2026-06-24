package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.application.port.out.ImpedimentTicketCreator;
import io.github.ctorressoftware.domain.model.ImpedimentTicket;

public class AzureDevOpsWorkItemTicketCreator implements ImpedimentTicketCreator {

    private final AzureDevOpsWorkItemClient azureDevOpsWorkItemClient;

    public AzureDevOpsWorkItemTicketCreator(AzureDevOpsWorkItemClient azureDevOpsWorkItemClient) {
        this.azureDevOpsWorkItemClient = azureDevOpsWorkItemClient;
    }

    public void create(ImpedimentTicket ticket) {

        var request = AzureDevOpsCreateWorkItemRequest.from(ticket);

        azureDevOpsWorkItemClient.createWorkItem(request);
    };
}
