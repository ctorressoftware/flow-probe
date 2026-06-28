package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.application.port.out.ImpedimentTicketCreator;
import io.github.ctorressoftware.domain.model.ImpedimentTicket;

public class AzureDevOpsImpedimentTicketCreatorAdapter implements ImpedimentTicketCreator {

    private final AzureDevOpsWorkItemTicketCreator azureDevOpsWorkItemTicketCreator;

    public AzureDevOpsImpedimentTicketCreatorAdapter(AzureDevOpsWorkItemTicketCreator azureDevOpsWorkItemTicketCreator) {
        this.azureDevOpsWorkItemTicketCreator = azureDevOpsWorkItemTicketCreator;
    }

    @Override
    public ImpedimentTicket create(ImpedimentTicket ticket) {
        var response = azureDevOpsWorkItemTicketCreator.create(ticket);
        return ImpedimentTicket.restore(
                (long) response.id(),
                response.fields().title(),
                response.fields().description()
        );
    }
}
