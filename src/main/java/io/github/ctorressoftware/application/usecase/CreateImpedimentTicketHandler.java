package io.github.ctorressoftware.application.usecase;

import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketCommand;
import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketResult;
import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketUseCase;
import io.github.ctorressoftware.application.port.out.ImpedimentTicketCreator;

public class CreateImpedimentTicketHandler implements CreateImpedimentTicketUseCase {

    private final ImpedimentTicketCreator impedimentTicketCreator;

    public CreateImpedimentTicketHandler(ImpedimentTicketCreator impedimentTicketCreator) {
        this.impedimentTicketCreator = impedimentTicketCreator;
    }

    @Override
    public CreateImpedimentTicketResult createTicket(CreateImpedimentTicketCommand command) {

        impedimentTicketCreator.create(command.impedimentTicket());

        return null;
    }
}
