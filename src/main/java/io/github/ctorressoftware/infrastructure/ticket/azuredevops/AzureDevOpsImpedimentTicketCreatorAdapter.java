package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.application.port.out.ImpedimentTicketCreator;
import io.github.ctorressoftware.domain.model.ImpedimentTicket;

public class AzureDevOpsImpedimentTicketCreatorAdapter implements ImpedimentTicketCreator {

    private AzureDevOpsWorkItemTicketCreator azureDevOpsWorkItemTicketCreator;

    public AzureDevOpsImpedimentTicketCreatorAdapter(AzureDevOpsWorkItemTicketCreator azureDevOpsWorkItemTicketCreator) {
        this.azureDevOpsWorkItemTicketCreator = azureDevOpsWorkItemTicketCreator;
    }

    @Override
    public void create(ImpedimentTicket ticket) {
        azureDevOpsWorkItemTicketCreator.create(ticket);
    }
}
