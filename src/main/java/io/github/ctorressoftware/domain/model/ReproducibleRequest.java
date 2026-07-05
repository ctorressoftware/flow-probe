package io.github.ctorressoftware.domain.model;

import java.util.Map;
import java.util.Objects;

public record ReproducibleRequest(
        String url,
        String method,
        Map<String, String> headers,
        String body
) {
    public static ReproducibleRequest fromServiceCall(ServiceCall serviceCall) {

        if (serviceCall == null) {
            throw new IllegalArgumentException("serviceCall cannot be null");
        }

        return new ReproducibleRequest(
                serviceCall.url(),
                serviceCall.method(),
                serviceCall.headers(),
                Objects.isNull(serviceCall.body()) ? null :  serviceCall.body().toString()
        );
    }
}