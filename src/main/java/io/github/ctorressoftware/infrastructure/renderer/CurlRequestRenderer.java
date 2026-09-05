package io.github.ctorressoftware.infrastructure.renderer;

import io.github.ctorressoftware.application.exception.JsonSerializationException;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.application.port.out.RequestRenderer;
import io.github.ctorressoftware.domain.model.ReproducibleRequest;
import io.github.ctorressoftware.domain.model.RequestFormat;
import io.github.ctorressoftware.infrastructure.renderer.exception.InvalidCurlBodyException;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CurlRequestRenderer implements RequestRenderer {

    private final JsonProcessor jsonProcessor;

    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization",
            "proxy-authorization",
            "cookie",
            "x-api-key",
            "api-key",
            "x-auth-token",
            "x-access-token",
            "x-amz-security-token"
    );

    public CurlRequestRenderer(JsonProcessor jsonProcessor) {
        this.jsonProcessor = Objects.requireNonNull(jsonProcessor);
    }

    @Override
    public String render(ReproducibleRequest request) {
        StringBuilder curl = new StringBuilder("curl");

        curl.append(" -X ").append(request.method());
        appendHeaders(curl, request.headers());
        appendBody(curl, request.body());
        curl.append(" ").append(singleQuote(request.url()));

        return curl.toString();
    }

    @Override
    public boolean supports(RequestFormat format) {
        return format == RequestFormat.CURL;
    }

    private void appendHeaders(StringBuilder curl, Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) return;

        headers.forEach((name, value) -> curl
                .append(" -H ")
                .append(singleQuote(name + ": " + sanitizeHeaderValue(name, value)))
        );
    }

    private void appendBody(StringBuilder curl, Object body) {
        try {
            if (body == null) return;
            String serialized = jsonProcessor.serialize(body);
            curl.append(" -d ").append(singleQuote(serialized));
        } catch (JsonSerializationException e) {
            throw new InvalidCurlBodyException(String.valueOf(body), e);
        }
    }

    private String singleQuote(String value) {
        return "'" + value.replace("'", "'\"'\"'") + "'";
    }

    private String sanitizeHeaderValue(String name, String value) {
        return SENSITIVE_HEADERS.contains(name.toLowerCase(Locale.ROOT))
                ? "<redacted>"
                : value;
    }
}
