package io.github.ctorressoftware.domain.model;

public record ServiceResponse(
        int status,
        String responseBody
) {}