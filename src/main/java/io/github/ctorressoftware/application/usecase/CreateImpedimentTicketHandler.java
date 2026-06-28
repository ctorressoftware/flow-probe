package io.github.ctorressoftware.application.usecase;

import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketCommand;
import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketResult;
import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketUseCase;
import io.github.ctorressoftware.application.port.out.ImpedimentTicketCreator;
import io.github.ctorressoftware.domain.model.ImpedimentTicket;

public class CreateImpedimentTicketHandler implements CreateImpedimentTicketUseCase {

    private final ImpedimentTicketCreator impedimentTicketCreator;

    public CreateImpedimentTicketHandler(ImpedimentTicketCreator impedimentTicketCreator) {
        this.impedimentTicketCreator = impedimentTicketCreator;
    }

    @Override
    public CreateImpedimentTicketResult createTicket(CreateImpedimentTicketCommand command) {
        ImpedimentTicket created = impedimentTicketCreator
                .create(command.impedimentTicket());
        return new CreateImpedimentTicketResult(created);
    }
}
