package io.github.ctorressoftware.application.port.out;

import io.github.ctorressoftware.domain.model.ImpedimentTicket;

public interface ImpedimentTicketCreator {
    void create(ImpedimentTicket ticket);
}
