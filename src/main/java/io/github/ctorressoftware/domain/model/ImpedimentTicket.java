package io.github.ctorressoftware.domain.model;

public record ImpedimentTicket(Long id, String title, String description) {

    public static ImpedimentTicket create(String title, String description) {
        return new ImpedimentTicket(null, title, description);
    }

    public static ImpedimentTicket restore(Long id, String title, String description) {
        return new ImpedimentTicket(id, title, description);
    }
}
