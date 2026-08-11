package io.github.ctorressoftware.infrastructure.renderer;

import io.github.ctorressoftware.application.exception.JsonSerializationException;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.application.port.out.RequestRenderer;
import io.github.ctorressoftware.domain.model.ReproducibleRequest;
import io.github.ctorressoftware.domain.model.RequestFormat;
import io.github.ctorressoftware.infrastructure.renderer.exception.InvalidCurlBodyException;

import java.util.Map;
import java.util.Objects;

public class CurlRequestRenderer implements RequestRenderer {

    private final JsonProcessor jsonProcessor;

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
                .append(singleQuote(name + ": " + value))
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
}
