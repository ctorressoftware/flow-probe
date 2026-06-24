package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.application.port.out.ImpedimentTicketCreator;
import io.github.ctorressoftware.domain.model.ImpedimentTicket;

public class AzureDevOpsImpedimentTicketCreatorAdapter implements ImpedimentTicketCreator {

    private AzureDevOpsWorkItemTicketCreator azureDevOpsTicketCreator;

    public AzureDevOpsImpedimentTicketCreatorAdapter(AzureDevOpsWorkItemTicketCreator azureDevOpsTicketCreator) {
        this.azureDevOpsTicketCreator = azureDevOpsTicketCreator;
    }

    @Override
    public void create(ImpedimentTicket ticket) {
        azureDevOpsTicketCreator.create(ticket);
    }
}
