package io.github.ctorressoftware.model;

import java.util.Map;

public record RequestFormat(
        String url,
        String method,
        Map<String, String> headers,
        Object body
) {}
