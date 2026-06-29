package io.github.ctorressoftware.infrastructure.renderer;

import io.github.ctorressoftware.application.port.out.RequestRenderer;
import io.github.ctorressoftware.domain.model.ReproducibleRequest;
import io.github.ctorressoftware.domain.model.RequestFormat;

import java.util.Map;

public class CurlRequestRenderer implements RequestRenderer {

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
        if (headers == null || headers.isEmpty()) {
            return;
        }

        headers.forEach((name, value) ->
                curl.append(" -H ")
                        .append(singleQuote(name + ": " + value))
        );
    }

    private void appendBody(StringBuilder curl, String body) {
        if (body == null || body.isBlank()) {
            return;
        }

        curl.append(" -d ")
                .append(singleQuote(body));
    }

    private String singleQuote(String value) {
        return "'" + value.replace("'", "'\"'\"'") + "'";
    }
}
