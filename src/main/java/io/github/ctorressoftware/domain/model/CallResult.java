package io.github.ctorressoftware.domain.model;

public record CallResult(
        int statusCode,
        String responseBody
        // TODO: add headers later // Map<String, List<String>> headers
) {}