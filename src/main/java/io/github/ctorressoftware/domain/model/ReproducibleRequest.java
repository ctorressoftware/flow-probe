package io.github.ctorressoftware.domain.model;

import java.util.Map;

public record ReproducibleRequest(
        String url,
        String method,
        Map<String, String> headers,
        String body
) {}