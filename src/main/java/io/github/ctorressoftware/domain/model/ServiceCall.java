package io.github.ctorressoftware.domain.model;

import java.util.Map;

/*
 * TODO: ServiceCall is currently designed for HTTP-style calls.
 * Revisit this design if FlowProbe needs to support other call types, such as gRPC over HTTP/2 or shell commands.
 */
public record ServiceCall(
        String url,
        String method,
        Map<String, String> headers,
        Object body
) {}
