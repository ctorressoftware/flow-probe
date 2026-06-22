package io.github.ctorressoftware.domain.model;

public class ImpedimentTicket {
    private final String title;
    private final String description;

    public ImpedimentTicket(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }
}
