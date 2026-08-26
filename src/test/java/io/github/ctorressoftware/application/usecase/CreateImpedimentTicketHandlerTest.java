package io.github.ctorressoftware.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
        // TODO
    }
}
