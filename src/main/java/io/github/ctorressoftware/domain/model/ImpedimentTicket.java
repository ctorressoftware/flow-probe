package io.github.ctorressoftware.domain.model;

public class ImpedimentTicket {
    private final Long id;
    private final String title;
    private final String description;

    private ImpedimentTicket(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public static ImpedimentTicket create(String title, String description) {
        return new ImpedimentTicket(null, title, description);
    }

    public static ImpedimentTicket restore(Long id, String title, String description) {
        return new ImpedimentTicket(id, title, description);
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }
}
