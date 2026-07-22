package io.github.ctorressoftware.domain.model;

import java.util.Map;

public record ReproducibleRequest(
        String url,
        String method,
        Map<String, String> headers,
        Object body
) {
    public static ReproducibleRequest fromServiceCall(ServiceCall serviceCall) {

        if (serviceCall == null) {
            throw new IllegalArgumentException("serviceCall cannot be null");
        }

        return new ReproducibleRequest(
                serviceCall.url(),
                serviceCall.method(),
                serviceCall.headers(),
                serviceCall.body()
        );
    }
}