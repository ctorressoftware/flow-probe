package io.github.ctorressoftware.domain.model;

public record ContextVariable(
        String name,
        Object value
        // String addedBy, // TODO: add this in a new iteration
        // boolean modifiable
) {}
