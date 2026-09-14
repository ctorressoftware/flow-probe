package io.github.ctorressoftware.domain.model;

public record ExpectationResult(
        boolean successful,
        Object expected,
        Object actual
) {}