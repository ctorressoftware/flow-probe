package io.github.ctorressoftware.domain.model;

public record CallResult(
        int status,
        String responseBody
) {}