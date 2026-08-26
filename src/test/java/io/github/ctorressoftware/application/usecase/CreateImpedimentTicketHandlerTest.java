package io.github.ctorressoftware.application.usecase;

import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketCommand;
import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketResult;
import io.github.ctorressoftware.domain.model.ImpedimentTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.ctorressoftware.application.port.out.ImpedimentTicketCreator;

@ExtendWith(MockitoExtension.class)
public class CreateImpedimentTicketHandlerTest {
    
    @Mock
    private ImpedimentTicketCreator impedimentTicketCreator;

    private CreateImpedimentTicketHandler createImpedimentTicketHandler;

    @BeforeEach
    void init() {
        this.createImpedimentTicketHandler = new CreateImpedimentTicketHandler(impedimentTicketCreator);
    }

    @Test
    void shouldCreateTicketAndReturnResult() {

        ImpedimentTicket ticket = ImpedimentTicket.create(
                "Title",
                "Description"
        );

        CreateImpedimentTicketCommand command = new CreateImpedimentTicketCommand(ticket);

        ImpedimentTicket expected = ImpedimentTicket.restore(
                123L,
                ticket.title(),
                ticket.description());

        Mockito
                .when(impedimentTicketCreator.create(ticket))
                .thenReturn(ImpedimentTicket.restore(
                        123L,
                        ticket.title(),
                        ticket.description()
                ));

        CreateImpedimentTicketResult result =
                createImpedimentTicketHandler.createTicket(command);

        Assertions.assertEquals(expected, result.created());
    }
}
